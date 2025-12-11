package io.github.nicolasfara.rstcovers.domain

import arrow.core.Either
import arrow.core.Nel
import arrow.core.raise.context.accumulate
import arrow.core.raise.context.bind
import arrow.core.raise.context.bindOrAccumulate
import arrow.core.raise.context.either
import arrow.core.raise.context.ensure
import io.github.nicolasfara.rstcovers.domain.errors.BusinessError
import io.github.nicolasfara.rstcovers.domain.errors.ValidationError
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlin.jvm.JvmInline
import kotlin.uuid.Uuid

@JvmInline
value class Email private constructor(val value: String) {
    companion object {
        private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@(.+)$")

        fun validate(email: String): Either<ValidationError, Unit> = either {
            ensure(email.matches(EMAIL_REGEX)) {
                ValidationError.InvalidEmail(email)
            }
        }

        operator fun invoke(value: String): Either<ValidationError, Email> = either {
            validate(value).bind()
            Email(value)
        }
    }
}

data class Address private constructor(
    val street: String,
    val city: String,
    val cap: String,
    val province: String
) {
    companion object {
        operator fun invoke(
            street: String,
            city: String,
            cap: String,
            province: String
        ): Either<Nel<ValidationError>, Address> = either {
            validate(street, city, cap, province).bind()
            Address(street, city, cap, province)
        }

        fun validate(
            street: String,
            city: String,
            cap: String,
            province: String
        ): Either<Nel<ValidationError>, Unit> = either {
            accumulate {
                ensure(street.isNotBlank()) { ValidationError.EmptyField("street") }
                ensure(city.isNotBlank()) { ValidationError.EmptyField("city") }
                ensure(cap.isNotBlank()) { ValidationError.EmptyField("CAP") }
                ensure(province.isNotBlank()) { ValidationError.EmptyField("province") }
            }
        }
    }
}

data class ContactInfo private constructor(
    val phone: String,
    val email: Email,
    val address: Address,
) {
    companion object {
        operator fun invoke(
            phone: String,
            email: Email,
            address: Address,
        ): Either<ValidationError, ContactInfo> = either {
            validate(phone).bind()
            ContactInfo(phone, email, address)
        }

        fun validate(phone: String): Either<ValidationError, Unit> = either {
            ensure(phone.isNotBlank()) { ValidationError.EmptyField("phone") }
        }
    }
}

@JvmInline
value class FiscalCode private constructor(val value: String) {
    companion object {
        operator fun invoke(value: String): Either<ValidationError, FiscalCode> = either {
            validate(value).bind()
            FiscalCode(value)
        }

        fun validate(value: String): Either<ValidationError, Unit> = either {
            ensure(value.length == 16) {
                ValidationError.InvalidFiscalCode(value)
            }
        }
    }
}

@JvmInline
value class BoatName private constructor(val value: String) {
    companion object {
        operator fun invoke(value: String): Either<ValidationError, BoatName> = either {
            validate(value).bind()
            BoatName(value)
        }

        fun validate(value: String): Either<ValidationError, Unit> = either {
            ensure(value.isNotBlank()) {
                ValidationError.EmptyField("boat name")
            }
        }
    }
}

enum class CustomerType {
    INDIVIDUAL,
    COMPANY,
    PUBLIC_ENTITY
}

fun String.validateCustomerType(): Either<ValidationError, Unit> = either {
    val sanitized = replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    ensure(CustomerType.entries.any { it.name == sanitized }) {
        ValidationError.InvalidCustomerType(this)
    }
}

fun String.toCustomerType(): CustomerType =
    CustomerType.valueOf(
        replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    )

// Employee-related Value Objects
@JvmInline
value class EmployeeId(val value: Uuid)

data class ContractPeriod private constructor(val startDate: LocalDate, val endDate: LocalDate?) {
    companion object {
        operator fun invoke(
            startDate: LocalDate,
            endDate: LocalDate?,
        ): Either<ValidationError, ContractPeriod> = either {
            validate(startDate, endDate).bind()
            ContractPeriod(startDate, endDate)
        }

        fun validate(
            startDate: LocalDate,
            endDate: LocalDate?,
        ): Either<ValidationError, Unit> = either {
            endDate?.let {
                ensure(it >= startDate) {
                    ValidationError.InvalidDate(
                        date = it.toString(),
                        reason = "End date cannot be before start date."
                    )
                }
            }
        }
    }

    fun isActive(onDate: LocalDate): Boolean {
        val isAfterStart = onDate >= startDate
        val isBeforeEnd = endDate?.let { onDate <= it } ?: true
        return isAfterStart && isBeforeEnd
    }
}


data class Week private constructor(val year: Int, val week: Int) {
    companion object {
        operator fun invoke(
            year: Int,
            weekNumber: Int,
        ): Either<ValidationError, Week> = either {
            validate(year, weekNumber).bind()
            Week(year, weekNumber)
        }

        fun validate(
            year: Int,
            weekNumber: Int,
        ): Either<ValidationError, Unit> = either {
            ensure(year >= 0) { ValidationError.InvalidYear(year) }
            ensure(weekNumber in 1..52) { ValidationError.InvalidWeek(weekNumber) }
        }
    }
}

data class OverrideBudget(val week: Week, val hours: Hours)

// Manufacturing-related Value Objects
@JvmInline
value class ManufacturingId(val value: Uuid)

@JvmInline
value class ManufacturingName private constructor(val value: String) {
    companion object {
        operator fun invoke(value: String): Either<ValidationError, ManufacturingName> = either {
            validate(value).bind()
            ManufacturingName(value)
        }

        fun validate(value: String): Either<ValidationError, Unit> = either {
            ensure(value.isNotBlank()) {
                ValidationError.EmptyField("manufacturing name")
            }
        }
    }
}

@JvmInline
value class ManufacturingCode private constructor(val value: String) {
    companion object {
        operator fun invoke(value: String): Either<ValidationError, ManufacturingCode> = either {
            validate(value).bind()
            ManufacturingCode(value)
        }

        fun validate(value: String): Either<ValidationError, Unit> = either {
            ensure(value.isNotBlank()) {
                ValidationError.EmptyField("manufacturing code")
            }
        }
    }
}

@JvmInline
value class ManufacturingDescription(val value: String?)

// Task-related Value Objects
@JvmInline
value class TaskId(val value: Uuid)

@JvmInline
value class TaskName private constructor(val value: String) {
    companion object {
        operator fun invoke(value: String): Either<ValidationError, TaskName> = either {
            validate(value).bind()
            TaskName(value)
        }

        fun validate(value: String): Either<ValidationError, Unit> = either {
            ensure(value.isNotBlank()) {
                ValidationError.EmptyField("task name")
            }
        }
    }
}

@JvmInline
value class Hours private constructor(val value: Int) {
    companion object {
        operator fun invoke(value: Int): Either<ValidationError, Hours> = either {
            validate(value).bind()
            Hours(value)
        }

        fun validate(value: Int): Either<ValidationError, Unit> = either {
            ensure(value >= 0) {
                ValidationError.InvalidHours(value, "Hours cannot be negative.")
            }
        }

        fun zero() = Hours(0)
    }
    operator fun plus(other: Hours): Hours = Hours(value + other.value)
    operator fun minus(other: Hours): Hours = Hours(value - other.value)
    operator fun compareTo(other: Hours): Int = value.compareTo(other.value)
}

enum class Priority {
    NORMAL,
    URGENT,
}

@JvmInline
value class ManufacturingNumber(val value: Uuid)

@JvmInline
value class DeliveryDate(val value: LocalDate) {
    fun targetCompletionDate(): LocalDate {
        val daysToSubtract = when (value.dayOfWeek) {
            DayOfWeek.MONDAY -> 0
            DayOfWeek.TUESDAY -> 1
            DayOfWeek.WEDNESDAY -> 2
            DayOfWeek.THURSDAY -> 3
            DayOfWeek.FRIDAY -> 4
            DayOfWeek.SATURDAY -> 5
            DayOfWeek.SUNDAY -> 6
        }
        val mondayOfWeek = value.minus(DatePeriod(days = daysToSubtract))
        return mondayOfWeek.minus(DatePeriod(days = 3))
    }
}

data class WeeklyCapacity private constructor(
    val week: Week,
    val capacityHours: Hours,
    val alreadyAllocatedHours: Hours = Hours.zero()
) {
    fun remainingCapacity(): Hours = capacityHours - alreadyAllocatedHours

    fun canAllocate(hours: Hours): Boolean = remainingCapacity() >= hours

    fun allocate(hours: Hours): Either<BusinessError, WeeklyCapacity> = either {
        ensure(canAllocate(hours)) {
            BusinessError.InsufficientCapacity(
                requiredCapacity = hours.value,
                availableCapacity = remainingCapacity().value
            )
        }
        copy(alreadyAllocatedHours = alreadyAllocatedHours + hours)
    }
}

@JvmInline
value class CustomerId(val value: Uuid)

@JvmInline
value class Name private constructor(val value: String) {
    companion object {
        operator fun invoke(value: String): Either<ValidationError, Name> = either {
            ensure(value.isNotBlank()) {
                ValidationError.EmptyField("customer name")
            }
            Name(value)
        }

        fun validate(value: String): Either<ValidationError, Unit> = either {
            ensure(value.isNotBlank()) {
                ValidationError.EmptyField("customer name")
            }
        }
    }
}

@JvmInline
value class Surname private constructor(val value: String) {
    companion object {
        operator fun invoke(value: String): Either<ValidationError, Surname> = either {
            ensure(value.isNotBlank()) {
                ValidationError.EmptyField("customer surname")
            }
            Surname(value)
        }

        fun validate(value: String): Either<ValidationError, Unit> = either {
            ensure(value.isNotBlank()) {
                ValidationError.EmptyField("customer surname")
            }
        }
    }
}

@JvmInline
value class OrderId(val value: Uuid)

data class PlanSlot(
    val taskId: TaskId,
    val orderId: OrderId,
    val week: Week,
    val allocatedHours: Hours,
    val priority: Priority,
)

@JvmInline
value class PlanId(val value: Uuid)

sealed interface PlanValidationResult {
    data object Valid : PlanValidationResult
    data class Invalid(val firstAvailableData: LocalDate) : PlanValidationResult
}
package io.github.nicolasfara.rstcovers.dtos.employee

import arrow.core.EitherNel
import arrow.core.raise.accumulate
import arrow.core.raise.context.bindOrAccumulate
import arrow.core.raise.either
import io.github.nicolasfara.rstcovers.domain.ContractPeriod
import io.github.nicolasfara.rstcovers.domain.EmployeeId
import io.github.nicolasfara.rstcovers.domain.Hours
import io.github.nicolasfara.rstcovers.domain.Name
import io.github.nicolasfara.rstcovers.domain.OverrideBudget
import io.github.nicolasfara.rstcovers.domain.Surname
import io.github.nicolasfara.rstcovers.domain.Week
import io.github.nicolasfara.rstcovers.domain.aggregates.Employee
import io.github.nicolasfara.rstcovers.domain.errors.ValidationError
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class EmployeeDTO(
    val id: Uuid,
    val name: String,
    val surname: String,
    val defaultBudgetHours: Int,
    val budgetHoursOverrides: List<OverrideBudgetDTO>,
    val contractStartDate: LocalDate,
    val contractEndDate: LocalDate?,
)

@Serializable
data class OverrideBudgetDTO(
    val year: Int,
    val weekNumber: Int,
    val hours: Int,
)

@Serializable
data class EmployeeCreationDTO(
    val name: String,
    val surname: String,
    val defaultBudgetHours: Int,
    val contractStartDate: LocalDate,
    val contractEndDate: LocalDate? = null,
) {
    fun toDomain(): EitherNel<ValidationError, Employee> = either {
        accumulate {
            Employee(
                id = EmployeeId(Uuid.random()),
                name = Name(name).bindOrAccumulate().value,
                surname = Surname(surname).bindOrAccumulate().value,
                defaultBudgetHours = Hours(defaultBudgetHours).bindOrAccumulate().value,
                budgetHoursOverrides = emptyList(),
                contractPeriod = ContractPeriod(contractStartDate, contractEndDate).bindOrAccumulate().value,
            )
        }
    }
}

@Serializable
data class EmployeeUpdateDTO(
    val name: String? = null,
    val surname: String? = null,
    val defaultBudgetHours: Int? = null,
    val contractStartDate: LocalDate? = null,
    val contractEndDate: LocalDate? = null,
)

@Serializable
data class PaginatedEmployeesDTO(
    val employees: List<EmployeeDTO>,
    val page: Long,
    val pageSize: Int,
    val totalItems: Long,
    val totalPages: Int,
)

fun Employee.toEmployeeDTO(): EmployeeDTO =
    EmployeeDTO(
        id = id.value,
        name = name.value,
        surname = surname.value,
        defaultBudgetHours = defaultBudgetHours.value,
        budgetHoursOverrides = budgetHoursOverrides.map { it.toDTO() },
        contractStartDate = contractPeriod.startDate,
        contractEndDate = contractPeriod.endDate,
    )

fun OverrideBudget.toDTO(): OverrideBudgetDTO =
    OverrideBudgetDTO(
        year = week.year,
        weekNumber = week.week,
        hours = hours.value,
    )

fun OverrideBudgetDTO.toDomain(): EitherNel<ValidationError, OverrideBudget> = either {
    accumulate {
        OverrideBudget(
            week = Week(year, weekNumber).bindOrAccumulate().value,
            hours = Hours(hours).bindOrAccumulate().value,
        )
    }
}

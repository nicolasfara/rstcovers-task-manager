package io.github.nicolasfara.rstcovers.domain.aggregates

import arrow.core.Either
import arrow.core.Nel
import arrow.core.nonEmptyListOf
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import io.github.nicolasfara.rstcovers.domain.CustomerId
import io.github.nicolasfara.rstcovers.domain.DeliveryDate
import io.github.nicolasfara.rstcovers.domain.Hours
import io.github.nicolasfara.rstcovers.domain.ManufacturingId
import io.github.nicolasfara.rstcovers.domain.OrderId
import io.github.nicolasfara.rstcovers.domain.Priority
import io.github.nicolasfara.rstcovers.domain.TaskId
import io.github.nicolasfara.rstcovers.domain.entities.Manufacturing
import io.github.nicolasfara.rstcovers.domain.errors.Error
import io.github.nicolasfara.rstcovers.domain.errors.NotFoundError
import io.github.nicolasfara.rstcovers.domain.errors.ValidationError
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

data class Order private constructor(
    val id: OrderId,
    val customerId: CustomerId,
    val createdAt: Instant,
    val deliveryDate: DeliveryDate,
    val productions: Nel<Manufacturing>,
    val priority: Priority
) {
    fun remainingProductionHours(): Hours {
        return productions.fold(Hours.zero()) { acc, manufacturing -> acc + manufacturing.remainingHours() }
    }

    fun totalProductionHours(): Hours = productions.fold(Hours.zero()) { acc, manufacturing ->
        acc + manufacturing.totalHours()
    }

    fun targetCompletionDate(): LocalDate = deliveryDate.targetCompletionDate()

    fun isCompleted(): Boolean = productions.all { it.isCompleted() }

    fun updateProduction(manufacturingId: ManufacturingId, taskId: TaskId, hours: Hours): Either<Error, Order> =
        either {
            val manufacturing = productions.find { it.id == manufacturingId }
            ensureNotNull(manufacturing) { NotFoundError.ManufacturingNotFound(manufacturingId.value) }
            val updatedManufacturing = manufacturing.updateTaskAdvancement(taskId, hours).bind()
            val newProductions = nonEmptyListOf(updatedManufacturing) + productions.filter { it.id != manufacturingId }
            copy(productions = newProductions)
        }

    fun changeDeliveryDate(newDeliveryDate: DeliveryDate): Either<ValidationError, Order> = either {
        val createdDate = createdAt.toLocalDateTime(TimeZone.UTC).date
        val newDate = newDeliveryDate.targetCompletionDate()
        ensure(newDate > createdDate) {
            ValidationError.InvalidDate(
                newDeliveryDate.value.toString(),
                "Delivery date must be after order creation date $createdDate")
        }
        copy(deliveryDate = newDeliveryDate)
    }

    fun changePriority(newPriority: Priority): Order = copy(priority = newPriority)
}

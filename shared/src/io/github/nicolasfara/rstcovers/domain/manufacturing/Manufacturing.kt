package io.github.nicolasfara.rstcovers.domain.manufacturing

import io.github.nicolasfara.rstcovers.domain.task.Hours
import io.github.nicolasfara.rstcovers.domain.customer.CustomerName
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
@JvmInline
value class ManufacturingId(val id: Uuid = Uuid.random())

@Serializable
data class Manufacturing(
    val id: ManufacturingId,
    val customer: CustomerName,
    val estimateHours: Hours,
    val completedHours: Hours = Hours(0.0),
    val deliveryDate: LocalDate,
    val creation: Instant,
) {
    fun remainingHours(): Hours = estimateHours - completedHours

    fun isCompleted(): Boolean = completedHours >= estimateHours

    fun updateCompletedHours(hours: Hours): Manufacturing {
        require(hours <= estimateHours) { "The remaining hours must be <= $estimateHours" }
        return copy(completedHours = hours)
    }

    fun isOverdue(now: Instant): Boolean = now >= creation && !isCompleted()

    fun daysUntilDeadline(currentData: Instant): Int =
        currentData.toLocalDateTime(TimeZone.currentSystemDefault()).date.daysUntil(deliveryDate)
}

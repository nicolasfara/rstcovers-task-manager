package io.github.nicolasfara.rstcovers.entities

import io.github.nicolasfara.rstcovers.valueobjects.Customer
import io.github.nicolasfara.rstcovers.valueobjects.Hours
import io.github.nicolasfara.rstcovers.valueobjects.TaskId
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

data class Task(
    val id: TaskId,
    val customer: Customer,
    val estimateHours: Hours,
    val completedHours: Hours = Hours(0.0),
    val deliveryDate: LocalDate,
    val creation: Instant,
) {
    fun remainingHours(): Hours = estimateHours - completedHours

    fun isCompleted(): Boolean = completedHours >= estimateHours

    fun updateCompletedHours(hours: Hours): Task {
        require(hours <= estimateHours) { "The remaining hours must be <= $estimateHours" }
        return copy(completedHours = hours)
    }

    fun isOverdue(now: Instant): Boolean = now >= creation && !isCompleted()

    fun daysUntilDeadline(currentData: Instant): Int =
        currentData.toLocalDateTime(TimeZone.currentSystemDefault()).date.daysUntil(deliveryDate)
}

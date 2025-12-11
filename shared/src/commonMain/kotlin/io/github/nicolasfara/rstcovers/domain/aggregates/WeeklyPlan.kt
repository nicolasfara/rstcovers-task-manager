package io.github.nicolasfara.rstcovers.domain.aggregates

import arrow.core.Either
import arrow.core.Nel
import arrow.core.raise.either
import arrow.core.raise.ensure
import io.github.nicolasfara.rstcovers.domain.Hours
import io.github.nicolasfara.rstcovers.domain.OrderId
import io.github.nicolasfara.rstcovers.domain.PlanId
import io.github.nicolasfara.rstcovers.domain.PlanSlot
import io.github.nicolasfara.rstcovers.domain.TaskId
import io.github.nicolasfara.rstcovers.domain.Week
import io.github.nicolasfara.rstcovers.domain.WeeklyCapacity
import io.github.nicolasfara.rstcovers.domain.errors.BusinessError
import kotlin.time.Instant

data class WeeklyPlan private constructor(
    val id: PlanId,
    val week: Week,
    val hoursCapacity: WeeklyCapacity,
    val slots: Nel<PlanSlot>,
    val createdAt: Instant,
) {
    companion object {
        operator fun invoke(
            id: PlanId,
            week: Week,
            hoursCapacity: WeeklyCapacity,
            slots: Nel<PlanSlot>,
            createdAt: Instant,
        ): Either<BusinessError, WeeklyPlan> = either {
            val allocatedHours = slots.fold(Hours.zero()) { acc, slot -> acc + slot.allocatedHours }
            ensure(allocatedHours <= hoursCapacity.capacityHours) {
                BusinessError.InsufficientCapacity(
                    requiredCapacity = allocatedHours.value,
                    availableCapacity = hoursCapacity.capacityHours.value
                )
            }
            WeeklyPlan(id, week, hoursCapacity, slots, createdAt)
        }
    }

    fun allocatedHours(): Hours = slots.fold(Hours.zero()) { acc, slot -> acc + slot.allocatedHours }

    fun availableHours(): Hours = hoursCapacity.capacityHours - allocatedHours()

    fun plannedTasks(): Nel<TaskId> = slots.map { it.taskId }

    fun slotsForOrder(orderId: OrderId): List<PlanSlot> = slots.filter { it.orderId == orderId }

    fun hoursForOrder(orderId: OrderId): Hours =
        slotsForOrder(orderId).fold(Hours.zero()) { acc, slot -> acc + slot.allocatedHours }

    fun containsTask(taskId: TaskId): Boolean = slots.any { it.taskId == taskId }
}

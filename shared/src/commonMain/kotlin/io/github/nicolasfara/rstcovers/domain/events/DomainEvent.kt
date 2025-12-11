package io.github.nicolasfara.rstcovers.domain.events

import io.github.nicolasfara.rstcovers.domain.CustomerId
import io.github.nicolasfara.rstcovers.domain.EmployeeId
import io.github.nicolasfara.rstcovers.domain.Hours
import io.github.nicolasfara.rstcovers.domain.ManufacturingId
import io.github.nicolasfara.rstcovers.domain.OrderId
import io.github.nicolasfara.rstcovers.domain.Priority
import io.github.nicolasfara.rstcovers.domain.TaskId
import io.github.nicolasfara.rstcovers.domain.Week
import kotlin.time.Instant
import kotlin.uuid.Uuid

sealed interface DomainEvent {
    val timestamp: Instant
    val aggregateId: Uuid
}

data class OrderInserted(
    override val timestamp: Instant,
    override val aggregateId: Uuid,
    val orderId: OrderId,
    val customerId: CustomerId,
    val deliveryDate: Instant,
    val totalHours: Hours,
    val priority: Priority,
): DomainEvent

data class OrderModified(
    override val timestamp: Instant,
    override val aggregateId: Uuid,
    val orderId: OrderId,
    val newDeliveryDate: Instant?,
    val newPriority: Priority?,
) : DomainEvent

data class TaskProgressed(
    override val timestamp: Instant,
    override val aggregateId: Uuid,
    val orderId: OrderId,
    val manufacturingId: ManufacturingId,
    val taskId: TaskId,
    val registeredHours: Hours,
) : DomainEvent

data class PlanRecomputed(
    override val timestamp: Instant,
    override val aggregateId: Uuid,
    val weeks: List<Week>,
) : DomainEvent

data class EmployeeBudgetUpdated(
    override val timestamp: Instant,
    override val aggregateId: Uuid,
    val employeeId: EmployeeId,
    val week: Week,
    val newBudgetHours: Hours,
) : DomainEvent

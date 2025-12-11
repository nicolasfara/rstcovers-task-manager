package io.github.nicolasfara.rstcovers.domain.errors

import kotlinx.datetime.LocalDate
import kotlin.uuid.Uuid

sealed interface Error {
    val message: String
}

sealed interface ValidationError : Error {
    data class InvalidEmail(val email: String) : ValidationError {
        override val message: String = "The provided email `$email` is not valid."
    }
    data class InvalidFiscalCode(val fiscalCode: String) : ValidationError {
        override val message: String = "The provided fiscal code `$fiscalCode` is not valid."
    }
    data class InvalidHours(val hours: Int, val reason: String) : ValidationError {
        override val message: String = "The provided hours `$hours` are not valid: $reason."
    }
    data class EmptyField(val fieldName: String) : ValidationError {
        override val message: String = "The field `$fieldName` cannot be empty."
    }
    data class InvalidDate(val date: String, val reason: String) : ValidationError {
        override val message: String = "The provided date `$date` is not valid: $reason."
    }
    data class InvalidWeek(val week: Int) : ValidationError {
        override val message: String = "The provided week `$week` is not valid. It must be between 1 and 52."
    }
    data class InvalidYear(val year: Int) : ValidationError {
        override val message: String = "The provided year `$year` is not valid. It must be a positive integer."
    }
    data class InvalidCustomerType(val type: String) : ValidationError {
        override val message: String = "The provided customer type `$type` is not valid."
    }
}

sealed interface BusinessError : Error {
    data class ExceededHours(val completedHours: Int, val estimatedHours: Int) : BusinessError {
        override val message: String = "The completed hours `$completedHours` exceed the estimated hours `$estimatedHours`."
    }
    data class OrderNotPlaceable(val orderId: Uuid, val firstUsefulDate: LocalDate) : BusinessError {
        override val message: String = "The order with ID `$orderId` cannot be placed before `$firstUsefulDate`."
    }
    data class ManufacturingWithoutTasks(val orderId: Uuid) : BusinessError {
        override val message: String = "The manufacturing for order ID `$orderId` cannot proceed without associated tasks."
    }
    data class InsufficientCapacity(val requiredCapacity: Int, val availableCapacity: Int) : BusinessError {
        override val message: String = "The required capacity `$requiredCapacity` exceeds the available capacity `$availableCapacity`."
    }
}

sealed interface NotFoundError : Error {
    data class CustomerNotFound(val userId: Uuid) : NotFoundError {
        override val message: String = "Client with ID `$userId` was not found."
    }
    data class EmployeeNotFound(val userId: Uuid) : NotFoundError {
        override val message: String = "Employee with ID `$userId` was not found."
    }
    data class OrderNotFound(val orderId: Uuid) : NotFoundError {
        override val message: String = "Order with ID `$orderId` was not found."
    }
    data class TaskNotFound(val taskId: Uuid) : NotFoundError {
        override val message: String = "Task with ID `$taskId` was not found."
    }
    data class ManufacturingNotFound(val manufacturingId: Uuid) : NotFoundError {
        override val message: String = "Manufacturing with ID `$manufacturingId` was not found."
    }
}

sealed interface InfrastructureError : Error

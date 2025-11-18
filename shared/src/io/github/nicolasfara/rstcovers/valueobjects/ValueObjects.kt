package io.github.nicolasfara.rstcovers.valueobjects

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline
import kotlin.uuid.Uuid

@Serializable
@JvmInline
value class OrderId(val id: Uuid = Uuid.random())

@Serializable
@JvmInline
value class TaskId(val id: Uuid = Uuid.random())

@Serializable
@JvmInline
value class ManufacturingId(val id: Uuid = Uuid.random())

@Serializable
@JvmInline
value class CustomerId(val id: Uuid = Uuid.random())

@Serializable
@JvmInline
value class CustomerName(val value: String) {
    init {
        require(value.isNotBlank()) { "Customer name must not be blank" }
    }
}

@Serializable
enum class CustomerType {
    INDIVIDUAL, COMPANY
}

@Serializable
@JvmInline
value class Email(val value: String) {
    init {
        require(value.contains("@")) { "Invalid email address" }
    }
}

@Serializable
@JvmInline
value class CellPhone(val number: String) {
    init {
        require(number.all { it.isDigit() || it == '+' || it == '-' || it.isWhitespace() }) {
            "Invalid cell phone number"
        }
    }
}

@Serializable
@JvmInline
value class FiscalCode(val value: String) {
    init {
        require(value.isNotBlank()) { "Fiscal code must not be blank" }
    }
}

@Serializable
@JvmInline
value class Address(val value: String) {
    init {
        require(value.isNotBlank()) { "Address must not be blank" }
    }
}

@Serializable
@JvmInline
value class Hours(val value: Double) {
    init {
        require(value >= 0.0) { "Hours must be positive" }
    }

    operator fun plus(other: Hours): Hours = Hours(value + other.value)
    operator fun minus(other: Hours): Hours = Hours(value - other.value)
    operator fun unaryMinus(): Hours = Hours(-value)
    operator fun compareTo(other: Hours): Int = value.compareTo(other.value)

    fun isZero(): Boolean = value == 0.0
    fun isGreaterThan(other: Hours): Boolean = value > other.value
}

@Serializable
@JvmInline
value class TaskName(val name: String) {
    init {
        require(name.isNotBlank()) { "Task name must not be blank" }
    }
}

@Serializable
enum class Priority {
    URGENT, NORMAL
}
package io.github.nicolasfara.rstcovers.valueobjects

import kotlin.jvm.JvmInline
import kotlin.uuid.Uuid

@JvmInline
value class TaskId(val id: Uuid = Uuid.random())

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

@JvmInline
value class Customer(val name: String) {
    init {
        require(name.isNotBlank()) { "Name must not be blank" }
    }
}

enum class Priority {
    URGENT, NORMAL
}
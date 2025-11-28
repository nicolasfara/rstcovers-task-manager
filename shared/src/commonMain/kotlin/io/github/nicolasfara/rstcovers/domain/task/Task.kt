package io.github.nicolasfara.rstcovers.domain.task

import kotlinx.serialization.Serializable
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
value class TaskName(val name: String) {
    init {
        require(name.isNotBlank()) { "Task name must not be blank" }
    }
}

enum class Priority {
    URGENT, NORMAL
}

data class Task(
    val id: TaskId,
    val name: TaskName,
    val hours: Hours,
    val requiresOtherTasks: List<TaskId> = emptyList(),
)
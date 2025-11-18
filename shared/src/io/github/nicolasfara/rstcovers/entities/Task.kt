package io.github.nicolasfara.rstcovers.entities

import io.github.nicolasfara.rstcovers.valueobjects.Hours
import io.github.nicolasfara.rstcovers.valueobjects.TaskId
import io.github.nicolasfara.rstcovers.valueobjects.TaskName
import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val id: TaskId,
    val name: TaskName,
    val hours: Hours,
    val requiresOtherTasks: List<TaskId> = emptyList(),
)
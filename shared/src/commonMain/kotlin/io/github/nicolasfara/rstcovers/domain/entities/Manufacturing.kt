package io.github.nicolasfara.rstcovers.domain.entities

import arrow.core.Either
import arrow.core.Nel
import arrow.core.nonEmptyListOf
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import io.github.nicolasfara.rstcovers.domain.Hours
import io.github.nicolasfara.rstcovers.domain.ManufacturingCode
import io.github.nicolasfara.rstcovers.domain.ManufacturingDescription
import io.github.nicolasfara.rstcovers.domain.ManufacturingId
import io.github.nicolasfara.rstcovers.domain.ManufacturingName
import io.github.nicolasfara.rstcovers.domain.TaskId
import io.github.nicolasfara.rstcovers.domain.errors.Error
import io.github.nicolasfara.rstcovers.domain.errors.NotFoundError

data class Manufacturing private constructor(
    val id: ManufacturingId,
    val name: ManufacturingName,
    val code: ManufacturingCode,
    val description: ManufacturingDescription,
    val tasks: Nel<Task>,
) {
    fun remainingHours(): Hours = tasks.fold(Hours.Companion.zero()) { acc, task -> acc + task.remainingHours() }

    fun totalHours(): Hours = tasks.fold(Hours.Companion.zero()) { acc, task -> acc + task.estimatedHours }

    fun isCompleted(): Boolean = tasks.all { it.isCompleted() }

    fun updateTaskAdvancement(taskId: TaskId, hours: Hours): Either<Error, Manufacturing> = either {
        val foundTask = tasks.find { it.id == taskId }
        ensureNotNull(foundTask) { NotFoundError.TaskNotFound(taskId.value) }
        foundTask.completeHours(hours).bind()
        val newTasks = nonEmptyListOf(foundTask) + tasks.filter { it.id != taskId }
        copy(tasks = newTasks)
    }
}

package io.github.nicolasfara.rstcovers.domain.entities

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import io.github.nicolasfara.rstcovers.domain.Hours
import io.github.nicolasfara.rstcovers.domain.TaskId
import io.github.nicolasfara.rstcovers.domain.TaskName
import io.github.nicolasfara.rstcovers.domain.errors.BusinessError

data class Task private constructor(
    val id: TaskId,
    val name: TaskName,
    val estimatedHours: Hours,
    val completedHours: Hours,
) {
    companion object {
        operator fun invoke(
            id: TaskId,
            name: TaskName,
            estimatedHours: Hours,
            completedHours: Hours,
        ): Either<BusinessError, Task> = either {
            ensure(completedHours <= estimatedHours) {
                BusinessError.ExceededHours(
                    completedHours = completedHours.value,
                    estimatedHours = estimatedHours.value,
                )
            }
            Task(
                id = id,
                name = name,
                estimatedHours = estimatedHours,
                completedHours = completedHours,
            )
        }
    }

    fun completeHours(hours: Hours): Either<BusinessError, Task> = either {
        val newCompletedHours = completedHours + hours
        ensure(newCompletedHours <= estimatedHours) {
            BusinessError.ExceededHours(
                completedHours = newCompletedHours.value,
                estimatedHours = estimatedHours.value,
            )
        }
        copy(completedHours = newCompletedHours)
    }

    fun remainingHours(): Hours = estimatedHours - completedHours

    fun isCompleted(): Boolean = completedHours == estimatedHours

    fun isPlannable(): Boolean = !isCompleted() && remainingHours() > Hours.Companion.zero()
}

package io.github.nicolasfara.rstcovers.domain.aggregates

import arrow.core.Nel
import io.github.nicolasfara.rstcovers.domain.Hours
import io.github.nicolasfara.rstcovers.domain.TaskId
import io.github.nicolasfara.rstcovers.domain.Week
import kotlin.time.Instant

data class GlobalPlan(
    val weeklyPlans: Nel<WeeklyPlan>,
    val createdAt: Instant,
) {
    fun weeklyPlanForWeek(week: Week): WeeklyPlan? = weeklyPlans.find { it.week == week }

    fun plannedTasks(): Set<TaskId> = weeklyPlans.flatMap { it.plannedTasks() }.toSet()

    fun weeksWithAvailableCapacity(): List<Week> =
        weeklyPlans.filter { it.availableHours() > Hours.zero() }.map { it.week }
}

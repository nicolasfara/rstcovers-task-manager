package io.github.nicolasfara.rstcovers.domain.aggregates

import io.github.nicolasfara.rstcovers.domain.ContractPeriod
import io.github.nicolasfara.rstcovers.domain.EmployeeId
import io.github.nicolasfara.rstcovers.domain.Hours
import io.github.nicolasfara.rstcovers.domain.Name
import io.github.nicolasfara.rstcovers.domain.OverrideBudget
import io.github.nicolasfara.rstcovers.domain.Surname
import io.github.nicolasfara.rstcovers.domain.Week
import kotlinx.datetime.LocalDate

// Aggregate root: Employee
data class Employee private constructor(
    val id: EmployeeId,
    val name: Name,
    val surname: Surname,
    val defaultBudgetHours: Hours,
    val budgetHoursOverrides: List<OverrideBudget>,
    val contractPeriod: ContractPeriod,
) {

    fun budgetHoursForWeek(week: Week): Hours {
        val override = budgetHoursOverrides.find { it.week == week }
        return override?.hours ?: defaultBudgetHours
    }

    fun isActiveOn(week: LocalDate): Boolean = contractPeriod.isActive(week)

    fun setOverrideBudgetHours(override: OverrideBudget): Employee {
        val updatedOverrides = budgetHoursOverrides.filter { it.week != override.week } + override
        return copy(budgetHoursOverrides = updatedOverrides)
    }
}

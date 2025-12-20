package io.github.nicolasfara.employee

import io.github.nicolasfara.rstcovers.domain.ContractPeriod
import io.github.nicolasfara.rstcovers.domain.EmployeeId
import io.github.nicolasfara.rstcovers.domain.Hours
import io.github.nicolasfara.rstcovers.domain.Name
import io.github.nicolasfara.rstcovers.domain.OverrideBudget
import io.github.nicolasfara.rstcovers.domain.Surname
import io.github.nicolasfara.rstcovers.domain.Week
import io.github.nicolasfara.rstcovers.domain.aggregates.Employee
import io.github.nicolasfara.errors.getOrThrow
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.core.ReferenceOption
import kotlinx.datetime.LocalDate
import kotlin.uuid.toKotlinUuid

object Employees : UUIDTable("employees") {
    val name = varchar("name", 255)
    val surname = varchar("surname", 255)
    val defaultBudgetHours = integer("default_budget_hours")
    val contractStartDate = varchar("contract_start_date", 10)
    val contractEndDate = varchar("contract_end_date", 10).nullable()
}

object EmployeeBudgetOverrides : IntIdTable("employee_budget_overrides") {
    val employeeId = reference("employee_id", Employees, onDelete = ReferenceOption.CASCADE)
    val year = integer("year")
    val weekNumber = integer("week_number")
    val hours = integer("hours")

    init {
        uniqueIndex(employeeId, year, weekNumber)
    }
}

fun rowToEmployee(row: ResultRow, overrides: List<OverrideBudget>): Employee =
    Employee(
        id = EmployeeId(row[Employees.id].value.toKotlinUuid()),
        name = Name(row[Employees.name]).getOrThrow(),
        surname = Surname(row[Employees.surname]).getOrThrow(),
        defaultBudgetHours = Hours(row[Employees.defaultBudgetHours]).getOrThrow(),
        budgetHoursOverrides = overrides,
        contractPeriod = ContractPeriod(
            LocalDate.parse(row[Employees.contractStartDate]),
            row[Employees.contractEndDate]?.let { LocalDate.parse(it) }
        ).getOrThrow(),
    )

fun rowToOverrideBudget(row: ResultRow): OverrideBudget =
    OverrideBudget(
        week = Week(row[EmployeeBudgetOverrides.year], row[EmployeeBudgetOverrides.weekNumber]).getOrThrow(),
        hours = Hours(row[EmployeeBudgetOverrides.hours]).getOrThrow(),
    )

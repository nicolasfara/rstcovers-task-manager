package io.github.nicolasfara.employee

import arrow.core.Either
import arrow.core.right
import io.github.nicolasfara.PostgresRepository
import io.github.nicolasfara.rstcovers.domain.EmployeeId
import io.github.nicolasfara.rstcovers.domain.aggregates.Employee
import io.github.nicolasfara.rstcovers.domain.errors.InfrastructureError
import io.github.nicolasfara.rstcovers.repository.EmployeeRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.update
import kotlin.uuid.toJavaUuid

class EmployeePostgresRepository : EmployeeRepository, PostgresRepository {
    override suspend fun getAllEmployees(): Either<InfrastructureError, List<Employee>> = dbQuery {
        val employees = Employees.selectAll().toList()
        val result = employees.map { row ->
            val id = row[Employees.id].value
            val overrides = EmployeeBudgetOverrides
                .selectAll()
                .where { EmployeeBudgetOverrides.employeeId eq id }
                .toList()
                .map(::rowToOverrideBudget)
            rowToEmployee(row, overrides)
        }
        result.right()
    }

    override suspend fun getEmployeesPaginated(page: Long, pageSize: Int): Either<InfrastructureError, List<Employee>> =
        dbQuery {
            val offset = (page - 1) * pageSize
            val employees = Employees.selectAll().limit(pageSize).offset(offset).toList()
            val result = employees.map { row ->
                val id = row[Employees.id].value
                val overrides = EmployeeBudgetOverrides
                    .selectAll()
                    .where { EmployeeBudgetOverrides.employeeId eq id }
                    .toList()
                    .map(::rowToOverrideBudget)
                rowToEmployee(row, overrides)
            }
            result.right()
        }

    override suspend fun countEmployees(): Either<InfrastructureError, Long> = dbQuery {
        Employees.selectAll().count().right()
    }

    override suspend fun save(employee: Employee): Either<InfrastructureError, EmployeeId> = dbQuery {
        Employees.insert {
            it[id] = employee.id.value.toJavaUuid()
            it[name] = employee.name.value
            it[surname] = employee.surname.value
            it[defaultBudgetHours] = employee.defaultBudgetHours.value
            it[contractStartDate] = employee.contractPeriod.startDate.toString()
            it[contractEndDate] = employee.contractPeriod.endDate?.toString()
        }

        employee.budgetHoursOverrides.forEach { override ->
            EmployeeBudgetOverrides.insert {
                it[employeeId] = employee.id.value.toJavaUuid()
                it[year] = override.week.year
                it[weekNumber] = override.week.week
                it[hours] = override.hours.value
            }
        }
        employee.id.right()
    }

    override suspend fun findById(id: EmployeeId): Either<InfrastructureError, Employee?> = dbQuery {
        val row = Employees.selectAll().where { Employees.id eq id.value.toJavaUuid() }.singleOrNull()
        if (row == null) {
            null.right()
        } else {
            val overrides = EmployeeBudgetOverrides
                .selectAll()
                .where { EmployeeBudgetOverrides.employeeId eq id.value.toJavaUuid() }
                .toList()
                .map(::rowToOverrideBudget)
            rowToEmployee(row, overrides).right()
        }
    }

    override suspend fun update(employee: Employee): Either<InfrastructureError, Unit> = dbQuery {
        Employees.update({ Employees.id eq employee.id.value.toJavaUuid() }) {
            it[name] = employee.name.value
            it[surname] = employee.surname.value
            it[defaultBudgetHours] = employee.defaultBudgetHours.value
            it[contractStartDate] = employee.contractPeriod.startDate.toString()
            it[contractEndDate] = employee.contractPeriod.endDate?.toString()
        }

        EmployeeBudgetOverrides.deleteWhere { EmployeeBudgetOverrides.employeeId eq employee.id.value.toJavaUuid() }
        employee.budgetHoursOverrides.forEach { override ->
            EmployeeBudgetOverrides.insert {
                it[employeeId] = employee.id.value.toJavaUuid()
                it[year] = override.week.year
                it[weekNumber] = override.week.week
                it[hours] = override.hours.value
            }
        }
        Unit.right()
    }

    override suspend fun deleteById(id: EmployeeId): Either<InfrastructureError, Unit> = dbQuery {
        Employees.deleteWhere { Employees.id eq id.value.toJavaUuid() }
        Unit.right()
    }
}

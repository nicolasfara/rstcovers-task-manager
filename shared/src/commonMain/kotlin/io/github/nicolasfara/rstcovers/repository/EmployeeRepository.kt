package io.github.nicolasfara.rstcovers.repository

import arrow.core.Either
import io.github.nicolasfara.rstcovers.domain.EmployeeId
import io.github.nicolasfara.rstcovers.domain.aggregates.Employee
import io.github.nicolasfara.rstcovers.domain.errors.InfrastructureError

interface EmployeeRepository {
    suspend fun getAllEmployees(): Either<InfrastructureError, List<Employee>>

    suspend fun getEmployeesPaginated(
        page: Long,
        pageSize: Int,
    ): Either<InfrastructureError, List<Employee>>

    suspend fun countEmployees(): Either<InfrastructureError, Long>

    suspend fun save(employee: Employee): Either<InfrastructureError, EmployeeId>

    suspend fun findById(id: EmployeeId): Either<InfrastructureError, Employee?>

    suspend fun update(employee: Employee): Either<InfrastructureError, Unit>

    suspend fun deleteById(id: EmployeeId): Either<InfrastructureError, Unit>
}

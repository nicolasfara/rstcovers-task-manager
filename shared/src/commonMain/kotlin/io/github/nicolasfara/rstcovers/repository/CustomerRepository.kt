package io.github.nicolasfara.rstcovers.repository

import arrow.core.Either
import io.github.nicolasfara.rstcovers.domain.CustomerId
import io.github.nicolasfara.rstcovers.domain.Email
import io.github.nicolasfara.rstcovers.domain.FiscalCode
import io.github.nicolasfara.rstcovers.domain.entities.Customer
import io.github.nicolasfara.rstcovers.domain.errors.InfrastructureError

interface CustomerRepository {
    suspend fun getAllCustomers(): Either<InfrastructureError, List<Customer>>

    suspend fun getCustomersPaginated(
        page: Long,
        pageSize: Int,
    ): Either<InfrastructureError, List<Customer>>

    suspend fun countCustomers(): Either<InfrastructureError, Long>

    suspend fun exists(
        email: Email,
        fiscalCode: FiscalCode,
    ): Either<InfrastructureError, Boolean>

    suspend fun save(customer: Customer): Either<InfrastructureError, CustomerId>

    suspend fun findById(id: CustomerId): Either<InfrastructureError, Customer?>

    suspend fun update(customer: Customer): Either<InfrastructureError, Unit>

    suspend fun deleteById(id: CustomerId): Either<InfrastructureError, Unit>
}

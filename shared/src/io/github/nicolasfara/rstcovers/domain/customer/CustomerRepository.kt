package io.github.nicolasfara.rstcovers.domain.customer

import arrow.core.Either
import io.github.nicolasfara.rstcovers.repository.RepositoryError.*

interface CustomerRepository {
    suspend fun getAllCustomers(): Either<PersistenceError, List<Customer>>

    suspend fun exists(email: Email, fiscalCode: FiscalCode): Either<PersistenceError, Boolean>

    suspend fun save(customer: Customer): Either<PersistenceError, CustomerId>

    suspend fun findById(id: CustomerId): Either<PersistenceError, Customer?>

    suspend fun update(customer: Customer): Either<PersistenceError, Unit>

    suspend fun deleteById(id: CustomerId): Either<PersistenceError, Unit>
}

package io.github.nicolasfara.customer

import arrow.core.Either
import arrow.core.left
import io.github.nicolasfara.rstcovers.domain.customer.Customer
import io.github.nicolasfara.rstcovers.domain.customer.CustomerId
import io.github.nicolasfara.rstcovers.domain.customer.CustomerRepository
import io.github.nicolasfara.rstcovers.domain.customer.Email
import io.github.nicolasfara.rstcovers.domain.customer.FiscalCode
import io.github.nicolasfara.rstcovers.repository.RepositoryError.*

class CustomerRepositorySql : CustomerRepository {
    override suspend fun getAllCustomers(): Either<PersistenceError, List<Customer>> {
        return PersistenceError("getAllCustomers is not implemented yet").left()
    }

    override suspend fun exists(
        email: Email,
        fiscalCode: FiscalCode
    ): Either<PersistenceError, Boolean> {
        return PersistenceError("exists is not implemented yet").left()
    }

    override suspend fun save(customer: Customer): Either<PersistenceError, CustomerId> {
        return PersistenceError("save is not implemented yet").left()
    }

    override suspend fun findById(id: CustomerId): Either<PersistenceError, Customer?> {
        return PersistenceError("findById is not implemented yet").left()
    }

    override suspend fun update(customer: Customer): Either<PersistenceError, Unit> {
        return PersistenceError("update is not implemented yet").left()
    }

    override suspend fun deleteById(id: CustomerId): Either<PersistenceError, Unit> {
        return PersistenceError("deleteById is not implemented yet").left()
    }
}
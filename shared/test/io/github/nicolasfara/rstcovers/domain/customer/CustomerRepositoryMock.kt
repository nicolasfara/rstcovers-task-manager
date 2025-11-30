package io.github.nicolasfara.rstcovers.domain.customer

import arrow.core.Either
import arrow.core.right
import io.github.nicolasfara.rstcovers.repository.RepositoryError

class CustomerRepositoryMock : CustomerRepository {
    private val customers = mutableMapOf<CustomerId, Customer>()

    override suspend fun exists(
        email: Email,
        fiscalCode: FiscalCode
    ): Either<RepositoryError.PersistenceError, Boolean> {
        val exists = customers.values.any { it.email == email || it.fiscalCode == fiscalCode }
        return exists.right()
    }

    override suspend fun save(customer: Customer): Either<RepositoryError.PersistenceError, CustomerId> {
        customers[customer.id] = customer
        return customer.id.right()
    }

    override suspend fun findById(id: CustomerId): Either<RepositoryError.PersistenceError, Customer?> {
        return customers[id].right()
    }

    override suspend fun update(customer: Customer): Either<RepositoryError.PersistenceError, Unit> {
        customers[customer.id] = customer
        return Unit.right()
    }

    override suspend fun deleteById(id: CustomerId): Either<RepositoryError.PersistenceError, Unit> {
        customers.remove(id)
        return Unit.right()
    }

    // Helper methods for testing
    fun clear() {
        customers.clear()
    }

    override suspend fun getAllCustomers(): Either<RepositoryError.PersistenceError, List<Customer>> {
        return customers.values.toList().right()
    }

    override suspend fun getCustomersPaginated(
        page: Int,
        pageSize: Int,
    ): Either<RepositoryError.PersistenceError, List<Customer>> {
        val offset = (page - 1) * pageSize
        val paginatedCustomers = customers.values.toList().drop(offset).take(pageSize)
        return paginatedCustomers.right()
    }

    override suspend fun countCustomers(): Either<RepositoryError.PersistenceError, Long> {
        return customers.size.toLong().right()
    }
}
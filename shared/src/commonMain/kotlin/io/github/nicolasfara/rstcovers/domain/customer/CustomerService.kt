package io.github.nicolasfara.rstcovers.domain.customer

import arrow.core.Either
import arrow.core.raise.either
import io.github.nicolasfara.rstcovers.repository.RepositoryError

sealed class CustomerError(
    reason: String?,
) : Throwable(reason) {
    data class CustomerAlreadyExists(
        val name: CustomerName,
    ) : CustomerError("Customer with name ${name.value} already exists")

    data class CustomerNotFound(
        val id: CustomerId,
    ) : CustomerError("Customer with id ${id.value} not found")

    data class CustomerRepositoryError(
        val error: RepositoryError,
    ) : CustomerError(error.toString())
}

class CustomerService(
    private val repository: CustomerRepository,
) {
    suspend fun createCustomer(
        name: CustomerName,
        email: Email,
        cellPhone: CellPhone,
        fiscalCode: FiscalCode,
        address: Address,
        type: CustomerType,
    ): Either<CustomerError, CustomerId> =
        either {
            if (repository.exists(email, fiscalCode).toCustomerError().bind()) {
                raise(CustomerError.CustomerAlreadyExists(name))
            }
            val customer =
                Customer(
                    id = CustomerId(),
                    name = name,
                    email = email,
                    cellPhone = cellPhone,
                    fiscalCode = fiscalCode,
                    address = address,
                    customerType = type,
                )
            repository.save(customer).toCustomerError().bind()
            customer.id
        }

    suspend fun getAllCustomers(): Either<CustomerError, List<Customer>> =
        either {
            repository.getAllCustomers().toCustomerError().bind()
        }

    suspend fun getCustomersPaginated(
        page: Long,
        pageSize: Int,
    ): Either<CustomerError, Pair<List<Customer>, Long>> =
        either {
            val customers = repository.getCustomersPaginated(page, pageSize).toCustomerError().bind()
            val total = repository.countCustomers().toCustomerError().bind()
            Pair(customers, total)
        }

    suspend fun getCustomer(id: CustomerId): Either<CustomerError, Customer> =
        either {
            val customer = repository.findById(id).toCustomerError().bind() ?: raise(CustomerError.CustomerNotFound(id))
            customer
        }

    suspend fun updateCustomer(customer: Customer): Either<CustomerError, Unit> =
        either {
            repository.findById(customer.id).toCustomerError().bind()
                ?: raise(CustomerError.CustomerNotFound(customer.id))
            repository.update(customer).toCustomerError().bind()
        }

    suspend fun deleteCustomer(id: CustomerId): Either<CustomerError, Unit> =
        either {
            repository.findById(id).toCustomerError().bind() ?: raise(CustomerError.CustomerNotFound(id))
            repository.deleteById(id).toCustomerError().bind()
        }

    private fun <R> Either<RepositoryError, R>.toCustomerError(): Either<CustomerError, R> =
        mapLeft(CustomerError::CustomerRepositoryError)
}

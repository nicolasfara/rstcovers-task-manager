package io.github.nicolasfara.customer

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import io.github.nicolasfara.BaseDbRepository
import io.github.nicolasfara.rstcovers.domain.customer.Customer
import io.github.nicolasfara.rstcovers.domain.customer.CustomerId
import io.github.nicolasfara.rstcovers.domain.customer.CustomerRepository
import io.github.nicolasfara.rstcovers.domain.customer.Email
import io.github.nicolasfara.rstcovers.domain.customer.FiscalCode
import io.github.nicolasfara.rstcovers.repository.RepositoryError.*
import kotlinx.coroutines.flow.singleOrNull
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.selectAll
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

class CustomerRepositorySql : CustomerRepository, BaseDbRepository {
    override suspend fun getAllCustomers(): Either<PersistenceError, List<Customer>> {
        return PersistenceError("getAllCustomers is not implemented yet").left()
    }

    override suspend fun exists(email: Email, fiscalCode: FiscalCode): Either<PersistenceError, Boolean> = dbQuery {
        val customer = Customers.selectAll()
            .where { Customers.email.eq(email.value) or Customers.fiscalCode.eq(fiscalCode.value) }
            .singleOrNull()?.let(::rowToCustomer)
        Either.Right(customer != null)
    }

    override suspend fun save(customer: Customer): Either<PersistenceError, CustomerId> = dbQuery {
        val createdId = Customers.insert {
            it[id] = customer.id.id.toJavaUuid()
            it[name] = customer.name.value
            it[email] = customer.email.value
            it[fiscalCode] = customer.fiscalCode.value
            it[cellPhone] = customer.cellPhone.number
            it[address] = customer.address.value
            it[customerType] = customer.customerType.name
        } get Customers.id
        CustomerId(createdId.toKotlinUuid()).right()
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
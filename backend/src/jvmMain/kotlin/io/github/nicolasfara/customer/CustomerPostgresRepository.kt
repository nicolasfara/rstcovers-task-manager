package io.github.nicolasfara.customer

import arrow.core.Either
import arrow.core.right
import io.github.nicolasfara.PostgresRepository
import io.github.nicolasfara.rstcovers.domain.customer.Customer
import io.github.nicolasfara.rstcovers.domain.customer.CustomerId
import io.github.nicolasfara.rstcovers.domain.customer.CustomerRepository
import io.github.nicolasfara.rstcovers.domain.customer.Email
import io.github.nicolasfara.rstcovers.domain.customer.FiscalCode
import io.github.nicolasfara.rstcovers.repository.RepositoryError.*
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.update
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.take
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

class CustomerPostgresRepository :
    CustomerRepository,
    PostgresRepository {
    override suspend fun getAllCustomers(): Either<PersistenceError, List<Customer>> =
        dbQuery {
            val customers =
                Customers
                    .selectAll()
                    .toList()
                    .map(::rowToCustomer)
            customers.right()
        }

    override suspend fun getCustomersPaginated(
        page: Long,
        pageSize: Int,
    ): Either<PersistenceError, List<Customer>> =
        dbQuery {
            val offset = (page - 1) * pageSize
            // Use Flow operations drop/take which translate to LIMIT/OFFSET at the database level
            // Note: For consistent pagination, consider adding ORDER BY in the future
            val customers =
                Customers
                    .selectAll()
                    .limit(pageSize)
                    .offset(offset)
                    .toList()
                    .map(::rowToCustomer)
            customers.right()
        }

    override suspend fun countCustomers(): Either<PersistenceError, Long> =
        dbQuery {
            // Use Flow.count() which translates to COUNT(*) at database level
            val count = Customers.selectAll().count()
            count.right()
        }

    override suspend fun exists(
        email: Email,
        fiscalCode: FiscalCode,
    ): Either<PersistenceError, Boolean> =
        dbQuery {
            val customer =
                Customers
                    .selectAll()
                    .where { Customers.email.eq(email.value) or Customers.fiscalCode.eq(fiscalCode.value) }
                    .singleOrNull()
                    ?.let(::rowToCustomer)
            Either.Right(customer != null)
        }

    override suspend fun save(customer: Customer): Either<PersistenceError, CustomerId> =
        dbQuery {
            val createdId =
                Customers.insert {
                    it[id] = customer.id.value.toJavaUuid()
                    it[name] = customer.name.value
                    it[email] = customer.email.value
                    it[fiscalCode] = customer.fiscalCode.value
                    it[cellPhone] = customer.cellPhone.value
                    it[address] = customer.address.value
                    it[customerType] = customer.customerType.name
                } get Customers.id
            CustomerId(createdId.toKotlinUuid()).right()
        }

    override suspend fun findById(id: CustomerId): Either<PersistenceError, Customer?> =
        dbQuery {
            val customer =
                Customers
                    .selectAll()
                    .where { Customers.id.eq(id.value.toJavaUuid()) }
                    .singleOrNull()
                    ?.let(::rowToCustomer)
            customer.right()
        }

    override suspend fun update(customer: Customer): Either<PersistenceError, Unit> =
        dbQuery {
            Customers.update({ Customers.id.eq(customer.id.value.toJavaUuid()) }) {
                it[name] = customer.name.value
                it[email] = customer.email.value
                it[fiscalCode] = customer.fiscalCode.value
                it[cellPhone] = customer.cellPhone.value
                it[address] = customer.address.value
                it[customerType] = customer.customerType.name
            }
            Unit.right()
        }

    override suspend fun deleteById(id: CustomerId): Either<PersistenceError, Unit> =
        dbQuery {
            Customers.deleteWhere { Customers.id.eq(id.value.toJavaUuid()) }
            Unit.right()
        }
}

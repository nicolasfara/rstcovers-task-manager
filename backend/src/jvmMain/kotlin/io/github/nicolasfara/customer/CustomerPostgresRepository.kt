package io.github.nicolasfara.customer

import arrow.core.Either
import arrow.core.right
import io.github.nicolasfara.PostgresRepository
import io.github.nicolasfara.rstcovers.domain.CustomerId
import io.github.nicolasfara.rstcovers.domain.Email
import io.github.nicolasfara.rstcovers.domain.FiscalCode
import io.github.nicolasfara.rstcovers.domain.entities.Customer
import io.github.nicolasfara.rstcovers.domain.errors.InfrastructureError
import io.github.nicolasfara.rstcovers.repository.CustomerRepository
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.update
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

class CustomerPostgresRepository :
    CustomerRepository,
    PostgresRepository {
    override suspend fun getAllCustomers(): Either<InfrastructureError, List<Customer>> =
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
    ): Either<InfrastructureError, List<Customer>> =
        dbQuery {
            val offset = (page - 1) * pageSize
            val customers =
                Customers
                    .selectAll()
                    .limit(pageSize)
                    .offset(offset)
                    .toList()
                    .map(::rowToCustomer)
            customers.right()
        }

    override suspend fun countCustomers(): Either<InfrastructureError, Long> =
        dbQuery {
            // Use Flow.count() which translates to COUNT(*) at database level
            val count = Customers.selectAll().count()
            count.right()
        }

    override suspend fun exists(
        email: Email,
        fiscalCode: FiscalCode,
    ): Either<InfrastructureError, Boolean> =
        dbQuery {
            val customer =
                Customers
                    .selectAll()
                    .where { Customers.email.eq(email.value) or Customers.fiscalCode.eq(fiscalCode.value) }
                    .singleOrNull()
                    ?.let(::rowToCustomer)
            Either.Right(customer != null)
        }

    override suspend fun save(customer: Customer): Either<InfrastructureError, CustomerId> =
        dbQuery {
            val createdId =
                Customers.insert {
                    it[name] = customer.name.value
                    it[surname] = customer.surname.value
                    it[email] = customer.contactInfo.email.value
                    it[cellPhone] = customer.contactInfo.phone
                    it[street] = customer.contactInfo.address.street
                    it[city] = customer.contactInfo.address.city
                    it[cap] = customer.contactInfo.address.cap
                    it[province] = customer.contactInfo.address.province
                    it[boatName] = customer.boatName?.value
                    it[fiscalCode] = customer.fiscalCode.value
                    it[customerType] = customer.customerType
                } get Customers.id
            CustomerId(createdId.value.toKotlinUuid()).right()
        }

    override suspend fun findById(id: CustomerId): Either<InfrastructureError, Customer?> =
        dbQuery {
            val customer =
                Customers
                    .selectAll()
                    .where { Customers.id.eq(id.value.toJavaUuid()) }
                    .singleOrNull()
                    ?.let(::rowToCustomer)
            customer.right()
        }

    override suspend fun update(customer: Customer): Either<InfrastructureError, Unit> =
        dbQuery {
            Customers.update({ Customers.id.eq(customer.id.value.toJavaUuid()) }) {
                it[name] = customer.name.value
                it[surname] = customer.surname.value
                it[email] = customer.contactInfo.email.value
                it[cellPhone] = customer.contactInfo.phone
                it[street] = customer.contactInfo.address.street
                it[city] = customer.contactInfo.address.city
                it[cap] = customer.contactInfo.address.cap
                it[province] = customer.contactInfo.address.province
                it[boatName] = customer.boatName?.value
                it[fiscalCode] = customer.fiscalCode.value
                it[customerType] = customer.customerType
            }
            Unit.right()
        }

    override suspend fun deleteById(id: CustomerId): Either<InfrastructureError, Unit> =
        dbQuery {
            Customers.deleteWhere { Customers.id.eq(id.value.toJavaUuid()) }
            Unit.right()
        }
}

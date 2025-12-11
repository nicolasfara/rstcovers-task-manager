package io.github.nicolasfara.customer

import arrow.core.Either
import arrow.core.Nel
import io.github.nicolasfara.rstcovers.domain.Address
import io.github.nicolasfara.rstcovers.domain.BoatName
import io.github.nicolasfara.rstcovers.domain.ContactInfo
import io.github.nicolasfara.rstcovers.domain.CustomerId
import io.github.nicolasfara.rstcovers.domain.CustomerType
import io.github.nicolasfara.rstcovers.domain.Email
import io.github.nicolasfara.rstcovers.domain.FiscalCode
import io.github.nicolasfara.rstcovers.domain.Name
import io.github.nicolasfara.rstcovers.domain.Surname
import io.github.nicolasfara.rstcovers.domain.entities.Customer
import io.github.nicolasfara.rstcovers.domain.errors.Error
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import kotlin.uuid.toKotlinUuid

object Customers : UUIDTable("customers") {
    val name = varchar("name", 255)
    val surname = varchar("surname", 255)
    val email = varchar("email", 255).uniqueIndex()
    val cellPhone = varchar("cell_phone", 20)
    val street = varchar("street", 255)
    val city = varchar("city", 100)
    val cap = varchar("cap", 20)
    val province = varchar("province", 100)
    val boatName = varchar("boat_name", 255).nullable()
    val fiscalCode = varchar("fiscal_code", 50).uniqueIndex()
    val customerType = enumeration<CustomerType>("customer_type")
}

private fun <E: Error, A> Either<E, A>.getOrThrow(): A =
    when (this) {
        is Either.Left -> error("Data layer corrupted: ${this.value.message}")
        is Either.Right -> this.value
    }

private fun <E: Error, A> Either<Nel<E>, A>.getOrThrowAll(): A =
    when (this) {
        is Either.Left -> error("Data layer corrupted: ${this.value.joinToString { it.message }}")
        is Either.Right -> this.value
    }

fun rowToCustomer(row: ResultRow): Customer =
    Customer(
        id = CustomerId(row[Customers.id].value.toKotlinUuid()),
        name = Name(row[Customers.name]).getOrThrow(),
        surname = Surname(row[Customers.surname]).getOrThrow(),
        fiscalCode = FiscalCode(row[Customers.fiscalCode]).getOrThrow(),
        contactInfo = ContactInfo(
            email = Email(row[Customers.email]).getOrThrow(),
            phone = row[Customers.cellPhone],
            address = Address(
                street = row[Customers.street],
                city = row[Customers.city],
                cap = row[Customers.cap],
                province = row[Customers.province],
            ).getOrThrowAll(),
        ).getOrThrow(),
        boatName = row[Customers.boatName]?.let { BoatName(it).getOrThrow() },
        customerType = row[Customers.customerType]
    )

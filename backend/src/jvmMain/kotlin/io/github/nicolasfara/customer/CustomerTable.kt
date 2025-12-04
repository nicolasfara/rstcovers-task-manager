package io.github.nicolasfara.customer

import io.github.nicolasfara.rstcovers.domain.customer.Address
import io.github.nicolasfara.rstcovers.domain.customer.CellPhone
import io.github.nicolasfara.rstcovers.domain.customer.Customer
import io.github.nicolasfara.rstcovers.domain.customer.CustomerId
import io.github.nicolasfara.rstcovers.domain.customer.CustomerName
import io.github.nicolasfara.rstcovers.domain.customer.CustomerType
import io.github.nicolasfara.rstcovers.domain.customer.Email
import io.github.nicolasfara.rstcovers.domain.customer.FiscalCode
import io.github.nicolasfara.rstcovers.domain.customer.coerceToCustomerType
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import kotlin.uuid.toKotlinUuid

object Customers : UUIDTable("customers") {
    val name = varchar("name", 255)
    val email = varchar("email", 255).uniqueIndex()
    val fiscalCode = varchar("fiscal_code", 50).uniqueIndex()
    val cellPhone = varchar("cell_phone", 20)
    val address = varchar("address", 500)
    val customerType = enumeration<CustomerType>("customer_type")
}

fun rowToCustomer(row: ResultRow): Customer =
    Customer(
        id = CustomerId(row[Customers.id].value.toKotlinUuid()),
        name = CustomerName.coerce(row[Customers.name]),
        email = Email.coerce(row[Customers.email]),
        fiscalCode = FiscalCode.coerce(row[Customers.fiscalCode]),
        cellPhone = CellPhone.coerce(row[Customers.cellPhone]),
        address = Address.coerce(row[Customers.address]),
        customerType = row[Customers.customerType],
    )

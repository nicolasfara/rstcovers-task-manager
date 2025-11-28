package io.github.nicolasfara.customer

import arrow.core.Either
import io.github.nicolasfara.rstcovers.domain.customer.Address
import io.github.nicolasfara.rstcovers.domain.customer.CellPhone
import io.github.nicolasfara.rstcovers.domain.customer.Customer
import io.github.nicolasfara.rstcovers.domain.customer.CustomerId
import io.github.nicolasfara.rstcovers.domain.customer.CustomerName
import io.github.nicolasfara.rstcovers.domain.customer.CustomerType
import io.github.nicolasfara.rstcovers.domain.customer.Email
import io.github.nicolasfara.rstcovers.domain.customer.FiscalCode
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class CustomerDTO(
    val id: String,
    val name: String,
    val email: String,
    val fiscalCode: String,
    val cellPhone: String,
    val address: String,
    val customerType: String,
)

fun Customer.toCustomerDTO(): CustomerDTO = CustomerDTO(
    id = this.id.id.toString(),
    name = this.name.value,
    email = this.email.value,
    fiscalCode = this.fiscalCode.value,
    cellPhone = this.cellPhone.number,
    address = this.address.value,
    customerType = this.customerType.name,
)

@Serializable
data class CustomerCreationDTO(
    val name: String,
    val email: String,
    val fiscalCode: String,
    val cellPhone: String,
    val address: String,
    val customerType: String,
)

fun CustomerCreationDTO.toCustomer(): Either<Throwable, Customer> {
    return Either.catch {
        Customer(
            id = CustomerId(Uuid.random()),
            name = CustomerName(name),
            email = Email(this.email),
            fiscalCode = FiscalCode(this.fiscalCode),
            cellPhone = CellPhone(this.cellPhone),
            address = Address(this.address),
            customerType = when (customerType) {
                "INDIVIDUAL" -> CustomerType.INDIVIDUAL
                "BUSINESS" -> CustomerType.COMPANY
                else -> throw IllegalArgumentException("Invalid customer type: $customerType")
            },
        )
    }
}

@Serializable
data class CustomerUpdateDTO(
    val id: String,
    val name: String?,
    val email: String?,
    val fiscalCode: String?,
    val cellPhone: String?,
    val address: String?,
    val customerType: String?,
)
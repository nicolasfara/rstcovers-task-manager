package io.github.nicolasfara.customer

import io.github.nicolasfara.rstcovers.domain.customer.Customer
import kotlinx.serialization.Serializable

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
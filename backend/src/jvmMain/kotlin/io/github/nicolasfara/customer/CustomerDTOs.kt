package io.github.nicolasfara.customer

import arrow.core.raise.ExperimentalRaiseAccumulateApi
import arrow.core.raise.context.accumulate
import arrow.core.raise.context.bindOrAccumulate
import arrow.core.raise.context.either
import io.github.nicolasfara.rstcovers.domain.customer.Address.Companion.validateAddress
import io.github.nicolasfara.rstcovers.domain.customer.CellPhone.Companion.validateCellPhone
import io.github.nicolasfara.rstcovers.domain.customer.Customer
import io.github.nicolasfara.rstcovers.domain.customer.CustomerName.Companion.validateCustomerName
import io.github.nicolasfara.rstcovers.domain.customer.Email.Companion.validateEmail
import io.github.nicolasfara.rstcovers.domain.customer.FiscalCode.Companion.validateFiscalCode
import io.github.nicolasfara.rstcovers.domain.customer.validateCustomerType
import io.ktor.server.plugins.requestvalidation.ValidationResult
import kotlinx.serialization.Serializable

@Serializable
data class CustomerDTO(
    val name: String,
    val email: String,
    val fiscalCode: String,
    val cellPhone: String,
    val address: String,
    val customerType: String,
)

fun Customer.toCustomerDTO(): CustomerDTO =
    CustomerDTO(
        name = name.value,
        email = email.value,
        fiscalCode = fiscalCode.value,
        cellPhone = cellPhone.value,
        address = address.value,
        customerType = customerType.name,
    )

@Serializable
data class CustomerCreationDTO(
    val name: String,
    val email: String,
    val fiscalCode: String,
    val cellPhone: String,
    val address: String,
    val customerType: String,
) {
    companion object {
        @OptIn(ExperimentalRaiseAccumulateApi::class)
        fun CustomerCreationDTO.validate(): ValidationResult =
            either {
                accumulate {
                    name.validateCustomerName().bindOrAccumulate()
                    email.validateEmail().bindOrAccumulate()
                    fiscalCode.validateFiscalCode().bindOrAccumulate()
                    cellPhone.validateCellPhone().bindOrAccumulate()
                    address.validateAddress().bindOrAccumulate()
                    customerType.validateCustomerType().bindOrAccumulate()
                }
            }.fold(
                ifLeft = { errors -> ValidationResult.Invalid(errors.mapNotNull { it.message }.toList()) },
                ifRight = { ValidationResult.Valid },
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

@Serializable
data class PaginatedCustomersDTO(
    val customers: List<CustomerDTO>,
    val page: Long,
    val pageSize: Int,
    val totalItems: Long,
    val totalPages: Int,
)


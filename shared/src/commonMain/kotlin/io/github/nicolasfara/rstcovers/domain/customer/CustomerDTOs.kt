package io.github.nicolasfara.rstcovers.domain.customer

import arrow.core.EitherNel
import arrow.core.raise.ExperimentalRaiseAccumulateApi
import arrow.core.raise.context.accumulate
import arrow.core.raise.context.bindOrAccumulate
import arrow.core.raise.context.either
import io.github.nicolasfara.rstcovers.domain.customer.Address.Companion.validateAddress
import io.github.nicolasfara.rstcovers.domain.customer.CellPhone.Companion.validateCellPhone
import io.github.nicolasfara.rstcovers.domain.customer.CustomerName.Companion.validateCustomerName
import io.github.nicolasfara.rstcovers.domain.customer.Email.Companion.validateEmail
import io.github.nicolasfara.rstcovers.domain.customer.FiscalCode.Companion.validateFiscalCode
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
        private fun accumulateValidations(vararg checks: () -> arrow.core.Either<Throwable, Unit>): EitherNel<String, Unit> =
            either {
                accumulate {
                    for (check in checks) check().bindOrAccumulate()
                }
            }.mapLeft { nel -> nel.map { it.message ?: "Unknown Error" } }.map { }

        @OptIn(ExperimentalRaiseAccumulateApi::class)
        fun CustomerCreationDTO.validate(): EitherNel<String, Unit> =
            accumulateValidations(
                { name.validateCustomerName() },
                { email.validateEmail() },
                { fiscalCode.validateFiscalCode() },
                { cellPhone.validateCellPhone() },
                { address.validateAddress() },
                { customerType.validateCustomerType() }
            )
    }
}

@Serializable
data class CustomerUpdateDTO(
    val name: String? = null,
    val email: String? = null,
    val fiscalCode: String? = null,
    val cellPhone: String? = null,
    val address: String? = null,
    val customerType: String? = null,
)

@Serializable
data class PaginatedCustomersDTO(
    val customers: List<CustomerDTO>,
    val page: Long,
    val pageSize: Int,
    val totalItems: Long,
    val totalPages: Int,
)

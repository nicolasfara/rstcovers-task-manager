package io.github.nicolasfara.rstcovers.dtos.customer

import arrow.core.EitherNel
import arrow.core.raise.accumulate
import arrow.core.raise.context.bindNelOrAccumulate
import arrow.core.raise.context.bindOrAccumulate
import arrow.core.raise.either
import io.github.nicolasfara.rstcovers.domain.Address
import io.github.nicolasfara.rstcovers.domain.BoatName
import io.github.nicolasfara.rstcovers.domain.ContactInfo
import io.github.nicolasfara.rstcovers.domain.CustomerId
import io.github.nicolasfara.rstcovers.domain.Email
import io.github.nicolasfara.rstcovers.domain.FiscalCode
import io.github.nicolasfara.rstcovers.domain.Name
import io.github.nicolasfara.rstcovers.domain.Surname
import io.github.nicolasfara.rstcovers.domain.entities.Customer
import io.github.nicolasfara.rstcovers.domain.errors.ValidationError
import io.github.nicolasfara.rstcovers.domain.toCustomerType
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class CustomerDTO(
    val id: Uuid,
    val name: String,
    val surname: String,
    val fiscalCode: String,
    val contactInfo: ContactInfoDto,
    val boatName: String?,
    val customerType: String,
)

@Serializable
data class CustomerCreationDTO(
    val name: String,
    val surname: String,
    val fiscalCode: String,
    val email: String,
    val cellPhone: String,
    val street: String,
    val city: String,
    val cap: String,
    val province: String,
    val boatName: String? = null,
    val customerType: String,
) {
    fun toDomain(): EitherNel<ValidationError, Customer> = either {
        accumulate {
            val address = Address(
                street = street,
                city = city,
                cap = cap,
                province = province,
            ).bindNelOrAccumulate()
            Customer(
                id = CustomerId(Uuid.random()),
                name = Name(name).bindOrAccumulate().value,
                surname = Surname(surname).bindOrAccumulate().value,
                fiscalCode = FiscalCode(fiscalCode).bindOrAccumulate().value,
                contactInfo = ContactInfo(
                    email = Email(email).bindOrAccumulate().value,
                    phone = cellPhone,
                    address = address.value,
                ).bindOrAccumulate().value,
                boatName = boatName?.let { BoatName(it).bindOrAccumulate().value },
                customerType = customerType.toCustomerType(),
            )
        }
    }
}

fun Customer.toCustomerDTO(): CustomerDTO =
    CustomerDTO(
        id = id.value,
        name = name.value,
        surname = surname.value,
        fiscalCode = fiscalCode.value,
        contactInfo = ContactInfoDto(
            email = contactInfo.email.value,
            cellPhone = contactInfo.phone,
            address = AddressDto(
                street = contactInfo.address.street,
                city = contactInfo.address.city,
                cap = contactInfo.address.cap,
                province = contactInfo.address.province,
            )
        ),
        boatName = boatName?.value,
        customerType = customerType.name,
    )

//@Serializable
//data class CustomerCreationDTO(
//    val name: String,
//    val email: String,
//    val fiscalCode: String,
//    val cellPhone: String,
//    val address: String,
//    val customerType: String,
//) {
//    companion object {
//        @OptIn(ExperimentalRaiseAccumulateApi::class)
//        private fun accumulateValidations(vararg checks: () -> Either<Throwable, Unit>): EitherNel<String, Unit> =
//            either {
//                accumulate {
//                    for (check in checks) check().bindOrAccumulate()
//                }
//            }.mapLeft { nel -> nel.map { it.message ?: "Unknown Error" } }.map { }
//
//        @OptIn(ExperimentalRaiseAccumulateApi::class)
//        fun CustomerCreationDTO.validate(): EitherNel<String, Unit> =
//            accumulateValidations(
//                { name.validateCustomerName() },
//                { email.validateEmail() },
//                { fiscalCode.validateFiscalCode() },
//                { cellPhone.validateCellPhone() },
//                { address.validateAddress() },
//                { customerType.validateCustomerType() }
//            )
//    }
//}

@Serializable
data class CustomerUpdateDTO(
    val name: String? = null,
    val surname: String? = null,
    val fiscalCode: String? = null,
    val contactInfo: ContactInfoDto? = null,
    val boatName: String? = null,
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

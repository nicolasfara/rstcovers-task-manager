package io.github.nicolasfara.rstcovers.domain.customer

import arrow.core.Either
import arrow.core.raise.context.ensure
import arrow.core.raise.either
import kotlin.jvm.JvmInline
import kotlin.uuid.Uuid

@JvmInline
value class CustomerId(val id: Uuid = Uuid.random())

class EmptyCustomerName : Throwable("Customer name must not be empty")
@JvmInline
value class CustomerName private constructor(val value: String) {
    companion object {
        operator fun invoke(name: String): Either<EmptyCustomerName, CustomerName> = either {
            ensure(name.isNotBlank()) { EmptyCustomerName() }
            CustomerName(name)
        }
    }
}

class InvalidCustomerType : Throwable("Invalid customer type")
enum class CustomerType {
    INDIVIDUAL, COMPANY
}
fun String.toCustomerType(): Either<InvalidCustomerType, CustomerType> = either {
    when (uppercase()) {
        "INDIVIDUAL" -> CustomerType.INDIVIDUAL
        "COMPANY" -> CustomerType.COMPANY
        else -> raise(InvalidCustomerType())
    }
}

data class InvalidEmailAddress(val reason: String) : Throwable(reason)
@JvmInline
value class Email private constructor(val value: String) {
    companion object {
        operator fun invoke(email: String): Either<InvalidEmailAddress, Email> = either {
            ensure(isValidEmail(email)) { InvalidEmailAddress("Invalid email format") }
            Email(email)
        }

        private fun isValidEmail(email: String): Boolean {
            val emailRegex = "^[A-Za-z](.*)(@)(.+)(\\.)(.+)".toRegex()
            return email.matches(emailRegex)
        }
    }
}

class InvalidCellPhoneNumber : Throwable("Invalid cell phone number")
@JvmInline
value class CellPhone private constructor(val number: String) {
    companion object {
        operator fun invoke(number: String): Either<InvalidCellPhoneNumber, CellPhone> = either {
            ensure(isValidCellPhone(number)) { InvalidCellPhoneNumber() }
            CellPhone(number)
        }

        private fun isValidCellPhone(number: String): Boolean {
            val phoneRegex = "^\\+?[1-9]\\d{1,14}$".toRegex()
            return number.matches(phoneRegex)
        }
    }
}

class FiscalCodeEmpty : Throwable("Fiscal code must not be empty")
@JvmInline
value class FiscalCode private constructor(val value: String) {
    companion object {
        operator fun invoke(code: String): Either<FiscalCodeEmpty, FiscalCode> = either {
            ensure(code.isNotBlank()) { FiscalCodeEmpty() }
            FiscalCode(code)
        }
    }
}

class InvalidAddress : Throwable("Address must not be blank")

@JvmInline
value class Address private constructor(val value: String) {
    companion object {
        operator fun invoke(address: String): Either<InvalidAddress, Address> = either {
            ensure(address.isNotBlank()) { InvalidAddress() }
            Address(address)
        }
    }
}

data class Customer(
    val id: CustomerId,
    val name: CustomerName,
    val cellPhone: CellPhone,
    val email: Email,
    val address: Address,
    val fiscalCode: FiscalCode,
    val customerType: CustomerType,
)

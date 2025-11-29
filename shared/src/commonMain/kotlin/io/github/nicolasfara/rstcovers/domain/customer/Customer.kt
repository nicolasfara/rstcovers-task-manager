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
            ensure(isValidName(name)) { EmptyCustomerName() }
            CustomerName(name)
        }

        fun coerce(name: String): CustomerName = CustomerName(name)

        fun String.validateCustomerName(): Either<EmptyCustomerName, Unit> = either {
            ensure(isValidName(this@validateCustomerName)) { EmptyCustomerName() }
        }

        private fun isValidName(name: String): Boolean = name.isNotBlank()
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
fun String.coerceToCustomerType(): CustomerType = when (uppercase()) {
    "INDIVIDUAL" -> CustomerType.INDIVIDUAL
    "COMPANY" -> CustomerType.COMPANY
    else -> throw InvalidCustomerType()
}

fun String.validateCustomerType(): Either<InvalidCustomerType, Unit> = either {
    when (uppercase()) {
        "INDIVIDUAL", "COMPANY" -> {}
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

        fun coerce(email: String): Email = Email(email)

        fun String.validateEmail(): Either<InvalidEmailAddress, Unit> = either {
            ensure(isValidEmail(this@validateEmail)) { InvalidEmailAddress("Invalid email format") }
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

        fun coerce(number: String): CellPhone = CellPhone(number)

        fun String.validateCellPhone(): Either<InvalidCellPhoneNumber, Unit> = either {
            ensure(isValidCellPhone(this@validateCellPhone)) { InvalidCellPhoneNumber() }
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

        fun coerce(code: String): FiscalCode = FiscalCode(code)

        fun String.validateFiscalCode(): Either<FiscalCodeEmpty, Unit> = either {
            ensure(isValidFiscalCode(this@validateFiscalCode)) { FiscalCodeEmpty() }
        }

        private fun isValidFiscalCode(code: String): Boolean = code.isNotBlank()
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

        fun coerce(address: String): Address = Address(address)

        fun String.validateAddress(): Either<InvalidAddress, Unit> = either {
            ensure(isValidAddress(this@validateAddress)) { InvalidAddress() }
        }

        private fun isValidAddress(address: String): Boolean = address.isNotBlank()
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

package io.github.nicolasfara.rstcovers.domain.customer

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline
import kotlin.uuid.Uuid

@Serializable
@JvmInline
value class CustomerId(val id: Uuid = Uuid.random())

@Serializable
@JvmInline
value class CustomerName(val value: String) {
    init {
        require(value.isNotBlank()) { "Customer name must not be blank" }
    }
}

@Serializable
enum class CustomerType {
    INDIVIDUAL, COMPANY
}

@Serializable
@JvmInline
value class Email(val value: String) {
    init {
        require(value.contains("@")) { "Invalid email address" }
    }
}

@Serializable
@JvmInline
value class CellPhone(val number: String) {
    init {
        require(number.all { it.isDigit() || it == '+' || it == '-' || it.isWhitespace() }) {
            "Invalid cell phone number"
        }
    }
}

@Serializable
@JvmInline
value class FiscalCode(val value: String) {
    init {
        require(value.isNotBlank()) { "Fiscal code must not be blank" }
    }
}

@Serializable
@JvmInline
value class Address(val value: String) {
    init {
        require(value.isNotBlank()) { "Address must not be blank" }
    }
}

@Serializable
data class Customer(
    val id: CustomerId,
    val name: CustomerName,
    val cellPhone: CellPhone,
    val email: Email,
    val address: Address,
    val fiscalCode: FiscalCode,
    val customerType: CustomerType,
)

package io.github.nicolasfara.rstcovers.domain.customer

import kotlin.jvm.JvmInline
import kotlin.uuid.Uuid

@JvmInline
value class CustomerId(val id: Uuid = Uuid.random())

@JvmInline
value class CustomerName(val value: String) {
    init {
        require(value.isNotBlank()) { "Customer name must not be blank" }
    }
}

enum class CustomerType {
    INDIVIDUAL, COMPANY
}

@JvmInline
value class Email(val value: String) {
    init {
        require(value.contains("@")) { "Invalid email address" }
    }
}

@JvmInline
value class CellPhone(val number: String) {
    init {
        require(number.all { it.isDigit() || it == '+' || it == '-' || it.isWhitespace() }) {
            "Invalid cell phone number"
        }
    }
}

@JvmInline
value class FiscalCode(val value: String) {
    init {
        require(value.isNotBlank()) { "Fiscal code must not be blank" }
    }
}

@JvmInline
value class Address(val value: String) {
    init {
        require(value.isNotBlank()) { "Address must not be blank" }
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

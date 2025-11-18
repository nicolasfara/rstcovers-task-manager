package io.github.nicolasfara.rstcovers.entities

import io.github.nicolasfara.rstcovers.valueobjects.Address
import io.github.nicolasfara.rstcovers.valueobjects.CellPhone
import io.github.nicolasfara.rstcovers.valueobjects.CustomerId
import io.github.nicolasfara.rstcovers.valueobjects.CustomerName
import io.github.nicolasfara.rstcovers.valueobjects.CustomerType
import io.github.nicolasfara.rstcovers.valueobjects.Email
import io.github.nicolasfara.rstcovers.valueobjects.FiscalCode
import kotlinx.serialization.Serializable

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

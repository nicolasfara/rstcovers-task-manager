package io.github.nicolasfara.rstcovers.domain.entities

import io.github.nicolasfara.rstcovers.domain.BoatName
import io.github.nicolasfara.rstcovers.domain.ContactInfo
import io.github.nicolasfara.rstcovers.domain.CustomerId
import io.github.nicolasfara.rstcovers.domain.CustomerType
import io.github.nicolasfara.rstcovers.domain.FiscalCode
import io.github.nicolasfara.rstcovers.domain.Name
import io.github.nicolasfara.rstcovers.domain.Surname

data class Customer(
    val id: CustomerId,
    val name: Name,
    val surname: Surname,
    val fiscalCode: FiscalCode,
    val contactInfo: ContactInfo,
    val boatName: BoatName?,
    val customerType: CustomerType,
)

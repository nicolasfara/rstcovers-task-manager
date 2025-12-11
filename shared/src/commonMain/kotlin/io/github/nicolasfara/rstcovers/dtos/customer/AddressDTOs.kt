package io.github.nicolasfara.rstcovers.dtos.customer

import arrow.core.EitherNel
import io.github.nicolasfara.rstcovers.domain.Address
import io.github.nicolasfara.rstcovers.domain.errors.ValidationError
import kotlinx.serialization.Serializable

@Serializable
data class AddressDto(val street: String, val city: String, val cap: String, val province: String) {
    fun toDomain(): EitherNel<ValidationError, Address> = Address(street, city, cap, province)
}
package io.github.nicolasfara.rstcovers.dtos.customer

import arrow.core.Either
import arrow.core.EitherNel
import arrow.core.raise.context.accumulate
import arrow.core.raise.context.bindNelOrAccumulate
import arrow.core.raise.context.bindOrAccumulate
import arrow.core.raise.either
import io.github.nicolasfara.rstcovers.domain.ContactInfo
import io.github.nicolasfara.rstcovers.domain.Email
import io.github.nicolasfara.rstcovers.domain.errors.ValidationError
import kotlinx.serialization.Serializable

@Serializable
data class ContactInfoDto(val email: String, val cellPhone: String, val address: AddressDto) {
    fun toDomain(): EitherNel<ValidationError, ContactInfo> = either {
        accumulate {
            ContactInfo(
                email = Email(email).bindOrAccumulate().value,
                phone = cellPhone,
                address = address.toDomain().bindNelOrAccumulate().value,
            ).bindOrAccumulate()
        }.value
    }
}
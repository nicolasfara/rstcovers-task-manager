package io.github.nicolasfara.plugins

import arrow.core.raise.accumulate
import arrow.core.raise.context.bind
import arrow.core.raise.context.bindNelOrAccumulate
import arrow.core.raise.context.bindOrAccumulate
import arrow.core.raise.either
import io.github.nicolasfara.rstcovers.domain.Address
import io.github.nicolasfara.rstcovers.domain.BoatName
import io.github.nicolasfara.rstcovers.domain.ContactInfo
import io.github.nicolasfara.rstcovers.domain.Email
import io.github.nicolasfara.rstcovers.domain.FiscalCode
import io.github.nicolasfara.rstcovers.domain.Name
import io.github.nicolasfara.rstcovers.domain.Surname
import io.github.nicolasfara.rstcovers.domain.validateCustomerType
import io.github.nicolasfara.rstcovers.dtos.customer.CustomerCreationDTO
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.requestvalidation.ValidationResult

fun Application.configureValidation() {
    install(RequestValidation) {
        validate<CustomerCreationDTO> { value ->
            either {
                accumulate {
                    Name.validate(value.name).bindOrAccumulate()
                    Surname.validate(value.surname).bindOrAccumulate()
                    FiscalCode.validate(value.fiscalCode).bindOrAccumulate()
                    Email.validate(value.email).bindOrAccumulate()
                    ContactInfo.validate(value.cellPhone).bindOrAccumulate()
                    Address.validate(
                        street = value.street,
                        city = value.city,
                        cap = value.cap,
                        province = value.province,
                    ).bindNelOrAccumulate()
                    value.boatName?.let { BoatName.validate(it).bindOrAccumulate() }
                    value.customerType.validateCustomerType().bindOrAccumulate()
                }
            }.fold(
                ifLeft = { ValidationResult.Invalid(it.map { error -> error.message }) },
                ifRight = { ValidationResult.Valid }
            )
        }
    }
}

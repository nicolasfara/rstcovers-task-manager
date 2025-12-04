package io.github.nicolasfara.plugins

import io.github.nicolasfara.rstcovers.domain.customer.CustomerCreationDTO
import io.github.nicolasfara.rstcovers.domain.customer.CustomerCreationDTO.Companion.validate
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.requestvalidation.ValidationResult

fun Application.configureValidation() {
    install(RequestValidation) {
        validate<CustomerCreationDTO> { value -> value.validate().fold(
            ifLeft = { errors -> ValidationResult.Invalid(errors.toList()) },
            ifRight = { ValidationResult.Valid }
        ) }
    }
}

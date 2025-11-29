package io.github.nicolasfara.plugins

import io.github.nicolasfara.customer.CustomerCreationDTO
import io.github.nicolasfara.customer.CustomerCreationDTO.Companion.validate
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.requestvalidation.RequestValidation

fun Application.configureValidation() {
    install(RequestValidation) {
        validate<CustomerCreationDTO> { value -> value.validate() }
    }
}
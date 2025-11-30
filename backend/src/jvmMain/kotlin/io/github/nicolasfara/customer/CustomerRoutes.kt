@file:OptIn(ExperimentalRaiseAccumulateApi::class)

package io.github.nicolasfara.customer

import arrow.core.Either
import arrow.core.raise.ExperimentalRaiseAccumulateApi
import arrow.core.raise.context.accumulate
import arrow.core.raise.context.bind
import arrow.core.raise.context.bindOrAccumulate
import arrow.core.raise.context.either
import io.github.nicolasfara.rstcovers.domain.customer.Address
import io.github.nicolasfara.rstcovers.domain.customer.CellPhone
import io.github.nicolasfara.rstcovers.domain.customer.CustomerError
import io.github.nicolasfara.rstcovers.domain.customer.CustomerId
import io.github.nicolasfara.rstcovers.domain.customer.CustomerName
import io.github.nicolasfara.rstcovers.domain.customer.CustomerService
import io.github.nicolasfara.rstcovers.domain.customer.Email
import io.github.nicolasfara.rstcovers.domain.customer.FiscalCode
import io.github.nicolasfara.rstcovers.domain.customer.toCustomerType
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.resources.patch
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing

object CustomerRoutes {
    fun Routing.customerRoutes(customerService: CustomerService) {
        // CREATE - POST /customers
        post<CustomersResource> {
            either {
                accumulate {
                    val creationRequest = Either.catch { call.receive<CustomerCreationDTO>() }.bind()
                    val name = CustomerName(creationRequest.name).bindOrAccumulate()
                    val email = Email(creationRequest.email).bindOrAccumulate()
                    val fiscalCode = FiscalCode(creationRequest.fiscalCode).bindOrAccumulate()
                    val cellPhone = CellPhone(creationRequest.cellPhone).bindOrAccumulate()
                    val address = Address(creationRequest.address).bindOrAccumulate()
                    val customerType = creationRequest.customerType.toCustomerType().bindOrAccumulate()
                    customerService
                        .createCustomer(
                            name = name.value,
                            email = email.value,
                            fiscalCode = fiscalCode.value,
                            cellPhone = cellPhone.value,
                            address = address.value,
                            type = customerType.value,
                        ).bind()
                }
            }.fold(
                ifLeft = { errors ->
                    call.respond(HttpStatusCode.BadRequest, errors.map { it.message ?: "Unknown error" })
                },
                ifRight = { call.respond(HttpStatusCode.Created, it.value) },
            )
        }

        // READ (all) - GET /customers
        get<CustomersResource> {
            customerService.getAllCustomers().fold(
                ifLeft = { call.respond(HttpStatusCode.InternalServerError, listOf(it.message)) },
                ifRight = { customers -> call.respond(HttpStatusCode.OK, customers.map { it.toCustomerDTO() }) },
            )
        }

        // READ (one) - GET /customers/{id}
        get<CustomersResource.Id> { resource ->
            customerService
                .getCustomer(CustomerId(resource.id))
                .map { it.toCustomerDTO() }
                .fold(
                    ifLeft = { error ->
                        val errorType =
                            when (error) {
                                is CustomerError.CustomerRepositoryError -> HttpStatusCode.InternalServerError
                                else -> HttpStatusCode.NotFound
                            }
                        call.respond(errorType, listOf(error.message))
                    },
                    ifRight = { customer -> call.respond(HttpStatusCode.OK, customer) },
                )
        }

        // UPDATE - PUT /customers/{id}
        put<CustomersResource.Id> { resource ->
            // TODO: Implement full customer update
            // val customerId = resource.id
            // val updates = call.receive<UpdateCustomerRequest>()
            call.respond(HttpStatusCode.OK, mapOf("id" to resource.id.toString(), "message" to "Customer updated"))
        }

        // PARTIAL UPDATE - PATCH /customers/{id}
        patch<CustomersResource.Id> { resource ->
            // TODO: Implement partial customer update
            // val customerId = resource.id
            // val updates = call.receive<PatchCustomerRequest>()
            call.respond(HttpStatusCode.OK, mapOf("id" to resource.id.toString(), "message" to "Customer patched"))
        }

        // DELETE - DELETE /customers/{id}
        delete<CustomersResource.Id> {
            // TODO: Implement customer deletion
            // val customerId = it.id
            call.respond(HttpStatusCode.NoContent)
        }
    }
}

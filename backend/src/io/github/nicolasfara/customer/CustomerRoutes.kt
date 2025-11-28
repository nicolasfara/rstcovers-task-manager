package io.github.nicolasfara.customer

import io.github.nicolasfara.rstcovers.domain.customer.CustomerId
import io.github.nicolasfara.rstcovers.domain.customer.CustomerRepository
import io.github.nicolasfara.rstcovers.domain.customer.CustomerService
import io.ktor.http.HttpStatusCode
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
            customerService.createCustomer()
        }

        // READ (all) - GET /customers
        get<CustomersResource> {
            customerService.getAllCustomers().fold(
                ifLeft = { call.respond(HttpStatusCode.InternalServerError, it) },
                ifRight = { call.respond(HttpStatusCode.OK, it) }
            )
        }

        // READ (one) - GET /customers/{id}
        get<CustomersResource.Id> { resource ->
            // TODO: Implement getting single customer
            // val customerId = resource.id
            call.respond(HttpStatusCode.OK, mapOf("id" to resource.id.toString()))
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
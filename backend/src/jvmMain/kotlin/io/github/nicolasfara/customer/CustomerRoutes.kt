package io.github.nicolasfara.customer

import arrow.core.Either
import arrow.core.raise.ExperimentalRaiseAccumulateApi
import arrow.core.raise.context.accumulate
import arrow.core.raise.context.bind
import arrow.core.raise.context.bindNelOrAccumulate
import arrow.core.raise.context.bindOrAccumulate
import arrow.core.raise.context.either
import io.github.nicolasfara.errors.collectErrors
import io.github.nicolasfara.rstcovers.dtos.customer.CustomerCreationDTO
import io.github.nicolasfara.rstcovers.dtos.customer.CustomerDTO
import io.github.nicolasfara.rstcovers.dtos.customer.toCustomerDTO

import io.github.nicolasfara.rstcovers.repository.CustomerRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.resources.post
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing

object CustomerRoutes {
    fun Routing.customerRoutes(customerRepository: CustomerRepository) {
        // CREATE - POST /customers
        post<CustomersResource> {
            either {
                accumulate {
                    val creationRequest = Either.catch { call.receive<CustomerCreationDTO>() }
                        .mapLeft { it.collectErrors() }
                        .bind()
                    val customer = creationRequest.toDomain().bindNelOrAccumulate().value
                    customerRepository.save(customer).bindOrAccumulate().value
                }
            }.fold(
                ifLeft = { errors -> call.respond(HttpStatusCode.InternalServerError, errors) },
                ifRight = { call.respond(HttpStatusCode.Created, it.value) },
            )
        }

//        // READ (all) - GET /customers
        get<CustomersResource> { resource ->
            val customersResult = customerRepository.getAllCustomers()
            customersResult.fold(
                ifLeft = { error -> call.respond(HttpStatusCode.InternalServerError, listOf(error.message)) },
                ifRight = { customers ->
                    val customerDTOs = customers.map { it.toCustomerDTO() }
                    call.respond(HttpStatusCode.OK, customerDTOs)
                },
            )
        }
//            val page = if (resource.page < 1) 1 else resource.page
//            val pageSize = if (resource.pageSize !in 1..100) 10 else resource.pageSize
//
//            customerService.getCustomersPaginated(page, pageSize).fold(
//                ifLeft = { call.respond(HttpStatusCode.InternalServerError, listOf(it.message)) },
//                ifRight = { (customers, total) ->
//                    val totalPages = ((total + pageSize - 1) / pageSize).toInt()
//                    val response =
//                        PaginatedCustomersDTO(
//                            customers = customers.map { it.toCustomerDTO() },
//                            page = page,
//                            pageSize = pageSize,
//                            totalItems = total,
//                            totalPages = totalPages,
//                        )
//                    call.respond(HttpStatusCode.OK, response)
//                },
//            )
//        }
//
//        // READ (one) - GET /customers/{id}
//        get<CustomersResource.Id> { resource ->
//            customerService
//                .getCustomer(CustomerId(resource.id))
//                .map { it.toCustomerDTO() }
//                .fold(
//                    ifLeft = { error ->
//                        val errorType =
//                            when (error) {
//                                is CustomerError.CustomerRepositoryError -> HttpStatusCode.InternalServerError
//                                else -> HttpStatusCode.NotFound
//                            }
//                        call.respond(errorType, listOf(error.message))
//                    },
//                    ifRight = { customer -> call.respond(HttpStatusCode.OK, customer) },
//                )
//        }
//
//        // UPDATE - PUT /customers/{id}
//        put<CustomersResource.Id> { resource ->
//            // TODO: Implement full customer update
//            // val customerId = resource.id
//            // val updates = call.receive<UpdateCustomerRequest>()
//            call.respond(HttpStatusCode.OK, mapOf("id" to resource.id.toString(), "message" to "Customer updated"))
//        }
//
//        // PARTIAL UPDATE - PATCH /customers/{id}
//        patch<CustomersResource.Id> { resource ->
//            either {
//                accumulate {
//                    val newCustomerDto = Either.catch { call.receive<CustomerUpdateDTO>() }.bind()
//                    val customerId = CustomerId(resource.id)
//                    val oldCustomer = customerService.getCustomer(customerId).bindOrAccumulate().value
//                    val newName = newCustomerDto.name?.let { CustomerName(it).bindOrAccumulate().value }
//                    val newEmail = newCustomerDto.email?.let { Email(it).bindOrAccumulate().value }
//                    val newFiscalCode = newCustomerDto.fiscalCode?.let { FiscalCode(it).bindOrAccumulate().value }
//                    val newCellPhone = newCustomerDto.cellPhone?.let { CellPhone(it).bindOrAccumulate().value }
//                    val newAddress = newCustomerDto.address?.let { Address(it).bindOrAccumulate().value }
//                    val newCustomerType = newCustomerDto.customerType?.toCustomerType()?.bindOrAccumulate()?.value
//                    val newCustomer = Customer(
//                        id = customerId,
//                        name = newName ?: oldCustomer.name,
//                        email = newEmail ?: oldCustomer.email,
//                        fiscalCode = newFiscalCode ?: oldCustomer.fiscalCode,
//                        cellPhone = newCellPhone ?: oldCustomer.cellPhone,
//                        address = newAddress ?: oldCustomer.address,
//                        customerType = newCustomerType ?: oldCustomer.customerType,
//                    )
//                    customerService.updateCustomer(newCustomer).bind()
//                }
//            }.fold(
//                ifLeft = { errors ->
//                    call.respond(HttpStatusCode.BadRequest, errors.map { it.collectErrors() }.flatten())
//                },
//                ifRight = { call.respond(HttpStatusCode.OK) },
//            )
//        }
//
//        // DELETE - DELETE /customers/{id}
//        delete<CustomersResource.Id> { customerId ->
//            customerService.deleteCustomer(CustomerId(customerId.id)).fold(
//                ifLeft = { error -> call.respond(HttpStatusCode.InternalServerError, error.collectErrors()) },
//                ifRight = { call.respond(HttpStatusCode.NoContent) },
//            )
//        }
    }
}

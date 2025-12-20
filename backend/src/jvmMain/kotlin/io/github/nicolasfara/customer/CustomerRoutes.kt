package io.github.nicolasfara.customer

import arrow.core.Either
import arrow.core.raise.accumulate
import arrow.core.raise.context.accumulate
import arrow.core.raise.context.bind
import arrow.core.raise.context.bindNelOrAccumulate
import arrow.core.raise.context.bindOrAccumulate
import arrow.core.raise.context.raise
import arrow.core.raise.context.either
import io.github.nicolasfara.errors.collectErrors
import io.github.nicolasfara.rstcovers.dtos.customer.CustomerCreationDTO
import io.github.nicolasfara.rstcovers.dtos.customer.CustomerDTO
import io.github.nicolasfara.rstcovers.dtos.customer.PaginatedCustomersDTO
import io.github.nicolasfara.rstcovers.dtos.customer.toCustomerDTO

import io.github.nicolasfara.rstcovers.repository.CustomerRepository
import io.github.nicolasfara.rstcovers.domain.Address
import io.github.nicolasfara.rstcovers.domain.BoatName
import io.github.nicolasfara.rstcovers.domain.ContactInfo
import io.github.nicolasfara.rstcovers.domain.CustomerId
import io.github.nicolasfara.rstcovers.domain.Email
import io.github.nicolasfara.rstcovers.domain.FiscalCode
import io.github.nicolasfara.rstcovers.domain.Name
import io.github.nicolasfara.rstcovers.domain.Surname
import io.github.nicolasfara.rstcovers.domain.toCustomerType
import io.github.nicolasfara.rstcovers.domain.entities.Customer
import io.github.nicolasfara.rstcovers.dtos.customer.CustomerUpdateDTO
import io.ktor.server.resources.put
import io.ktor.server.resources.patch
import io.ktor.server.resources.delete
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
            val page = resource.page
            val pageSize = resource.pageSize

            either {
                when {
                    page == null && pageSize == null -> {
                        customerRepository.getAllCustomers()
                            .mapLeft { listOf(it.message) }
                            .bind()
                            .map { it.toCustomerDTO() }
                    }
                    page != null && pageSize != null -> {
                        val customers = customerRepository.getCustomersPaginated(page, pageSize)
                            .mapLeft { listOf(it.message) }
                            .bind()
                        val totalItems = customerRepository.countCustomers()
                            .mapLeft { listOf(it.message) }
                            .bind()
                        val totalPages = (totalItems + pageSize - 1) / pageSize

                        PaginatedCustomersDTO(
                            customers = customers.map { it.toCustomerDTO() },
                            page = page,
                            pageSize = pageSize,
                            totalItems = totalItems,
                            totalPages = totalPages.toInt()
                        )
                    }
                    else -> {
                        raise(listOf("Both page and pageSize must be provided for paginated requests"))
                    }
                }
            }.fold(
                ifLeft = { errors: List<String> ->
                    val status = if (page != null && pageSize == null || page == null && pageSize != null) {
                        HttpStatusCode.BadRequest
                    } else {
                        HttpStatusCode.InternalServerError
                    }
                    call.respond(status, errors)
                },
                ifRight = { call.respond(HttpStatusCode.OK, it) }
            )
        }
        // READ (one) - GET /customers/{id}
        get<CustomersResource.Id> { resource ->
            val customerId = CustomerId(resource.id)
            customerRepository.findById(customerId).fold(
                ifLeft = { error -> call.respond(HttpStatusCode.InternalServerError, listOf(error.message)) },
                ifRight = { customer ->
                    if (customer != null) {
                        call.respond(HttpStatusCode.OK, customer.toCustomerDTO())
                    } else {
                        call.respond(HttpStatusCode.NotFound, listOf("Customer with ID ${resource.id} not found"))
                    }
                },
            )
        }

        // UPDATE - PUT /customers/{id}
        put<CustomersResource.Id> { resource ->
            either {
                accumulate {
                    val updateRequest = Either.catch { call.receive<CustomerCreationDTO>() }
                        .mapLeft { it.collectErrors() }
                        .bind()
                    val customer = updateRequest.toDomain().bindNelOrAccumulate().value
                    val updatedCustomer = customer.copy(id = CustomerId(resource.id))
                    customerRepository.update(updatedCustomer).mapLeft { listOf(it.message) }.bind()
                }
            }.fold(
                ifLeft = { errors -> call.respond(HttpStatusCode.BadRequest, errors.map { it.toString() }) },
                ifRight = { call.respond(HttpStatusCode.OK) },
            )
        }

        // PARTIAL UPDATE - PATCH /customers/{id}
        patch<CustomersResource.Id> { resource ->
            either {
                accumulate {
                    val updateDto = Either.catch { call.receive<CustomerUpdateDTO>() }
                        .mapLeft { it.collectErrors() }
                        .bind()
                    val customerId = CustomerId(resource.id)
                    val existingCustomer = customerRepository.findById(customerId)
                        .mapLeft { listOf(it.message) }
                        .bind()
                        ?: raise(listOf("Customer with ID ${resource.id} not found"))

                    val newName = updateDto.name?.let { Name(it).bindOrAccumulate().value }
                    val newSurname = updateDto.surname?.let { Surname(it).bindOrAccumulate().value }
                    val newFiscalCode = updateDto.fiscalCode?.let { FiscalCode(it).bindOrAccumulate().value }
                    val newBoatName = updateDto.boatName?.let { BoatName(it).bindOrAccumulate().value }
                    val newCustomerType = updateDto.customerType?.toCustomerType()

                    val newEmail = updateDto.contactInfo?.email?.let { Email(it).bindOrAccumulate().value }
                    val newPhone = updateDto.contactInfo?.cellPhone
                    val newAddress = updateDto.contactInfo?.address?.let { addressDto ->
                        Address(
                            street = addressDto.street,
                            city = addressDto.city,
                            cap = addressDto.cap,
                            province = addressDto.province
                        ).bindNelOrAccumulate().value
                    }

                    val updatedCustomer = existingCustomer.copy(
                        name = newName ?: existingCustomer.name,
                        surname = newSurname ?: existingCustomer.surname,
                        fiscalCode = newFiscalCode ?: existingCustomer.fiscalCode,
                        boatName = newBoatName ?: existingCustomer.boatName,
                        customerType = newCustomerType ?: existingCustomer.customerType,
                        contactInfo = ContactInfo(
                            phone = newPhone ?: existingCustomer.contactInfo.phone,
                            email = newEmail ?: existingCustomer.contactInfo.email,
                            address = newAddress ?: existingCustomer.contactInfo.address
                        ).bindOrAccumulate().value
                    )
                    customerRepository.update(updatedCustomer).mapLeft { listOf(it.message) }.bind()
                }
            }.fold(
                ifLeft = { errors ->
                    val errorList = errors.map { it.toString() }
                    val status = if (errorList.any { it.contains("not found", ignoreCase = true) }) HttpStatusCode.NotFound else HttpStatusCode.BadRequest
                    call.respond(status, errorList)
                },
                ifRight = { call.respond(HttpStatusCode.OK) },
            )
        }

        // DELETE - DELETE /customers/{id}
        delete<CustomersResource.Id> { resource ->
            val customerId = CustomerId(resource.id)
            customerRepository.deleteById(customerId).fold(
                ifLeft = { error -> call.respond(HttpStatusCode.InternalServerError, listOf(error.message)) },
                ifRight = { call.respond(HttpStatusCode.NoContent) },
            )
        }
    }
}

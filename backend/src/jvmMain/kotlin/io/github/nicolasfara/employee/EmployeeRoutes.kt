package io.github.nicolasfara.employee

import arrow.core.Either
import arrow.core.raise.accumulate
import arrow.core.raise.context.accumulate
import arrow.core.raise.context.bind
import arrow.core.raise.context.bindNelOrAccumulate
import arrow.core.raise.context.bindOrAccumulate
import arrow.core.raise.context.either
import arrow.core.raise.context.raise
import io.github.nicolasfara.errors.collectErrors
import io.github.nicolasfara.rstcovers.domain.ContractPeriod
import io.github.nicolasfara.rstcovers.domain.EmployeeId
import io.github.nicolasfara.rstcovers.domain.Hours
import io.github.nicolasfara.rstcovers.domain.Name
import io.github.nicolasfara.rstcovers.domain.Surname
import io.github.nicolasfara.rstcovers.dtos.employee.*
import io.github.nicolasfara.rstcovers.repository.EmployeeRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.resources.*
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing

object EmployeeRoutes {
    fun Routing.employeeRoutes(employeeRepository: EmployeeRepository) {
        // CREATE - POST /employees
        post<EmployeesResource> {
            either {
                accumulate {
                    val creationRequest = Either.catch { call.receive<EmployeeCreationDTO>() }
                        .mapLeft { it.collectErrors() }
                        .bind()
                    val employee = creationRequest.toDomain().bindNelOrAccumulate().value
                    employeeRepository.save(employee).bindOrAccumulate().value
                }
            }.fold(
                ifLeft = { errors -> call.respond(HttpStatusCode.InternalServerError, errors) },
                ifRight = { call.respond(HttpStatusCode.Created, it.value) },
            )
        }

        // READ (all) - GET /employees
        get<EmployeesResource> { resource ->
            val page = resource.page
            val pageSize = resource.pageSize

            either {
                when {
                    page == null && pageSize == null -> {
                        employeeRepository.getAllEmployees()
                            .mapLeft { listOf(it.message) }
                            .bind()
                            .map { it.toEmployeeDTO() }
                    }

                    page != null && pageSize != null -> {
                        val employees = employeeRepository.getEmployeesPaginated(page, pageSize)
                            .mapLeft { listOf(it.message) }
                            .bind()
                        val totalItems = employeeRepository.countEmployees()
                            .mapLeft { listOf(it.message) }
                            .bind()
                        val totalPages = (totalItems + pageSize - 1) / pageSize

                        PaginatedEmployeesDTO(
                            employees = employees.map { it.toEmployeeDTO() },
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

        // READ (one) - GET /employees/{id}
        get<EmployeesResource.Id> { resource ->
            val employeeId = EmployeeId(resource.id)
            employeeRepository.findById(employeeId).fold(
                ifLeft = { error -> call.respond(HttpStatusCode.InternalServerError, listOf(error.message)) },
                ifRight = { employee ->
                    if (employee != null) {
                        call.respond(HttpStatusCode.OK, employee.toEmployeeDTO())
                    } else {
                        call.respond(HttpStatusCode.NotFound, listOf("Employee with ID ${resource.id} not found"))
                    }
                },
            )
        }

        // UPDATE - PUT /employees/{id}
        put<EmployeesResource.Id> { resource ->
            either {
                accumulate {
                    val updateRequest = Either.catch { call.receive<EmployeeCreationDTO>() }
                        .mapLeft { it.collectErrors() }
                        .bind()
                    val employee = updateRequest.toDomain().bindNelOrAccumulate().value
                    val updatedEmployee = employee.copy(id = EmployeeId(resource.id))
                    employeeRepository.update(updatedEmployee).mapLeft { listOf(it.message) }.bind()
                }
            }.fold(
                ifLeft = { errors -> call.respond(HttpStatusCode.BadRequest, errors.map { it.toString() }) },
                ifRight = { call.respond(HttpStatusCode.OK) },
            )
        }

        // PARTIAL UPDATE - PATCH /employees/{id}
        patch<EmployeesResource.Id> { resource ->
            either {
                accumulate {
                    val updateDto = Either.catch { call.receive<EmployeeUpdateDTO>() }
                        .mapLeft { it.collectErrors() }
                        .bind()
                    val employeeId = EmployeeId(resource.id)
                    val existingEmployee = employeeRepository.findById(employeeId)
                        .mapLeft { listOf(it.message) }
                        .bind()
                        ?: raise(listOf("Employee with ID ${resource.id} not found"))

                    val newName = updateDto.name?.let { Name(it).bindOrAccumulate().value }
                    val newSurname = updateDto.surname?.let { Surname(it).bindOrAccumulate().value }
                    val newDefaultBudgetHours =
                        updateDto.defaultBudgetHours?.let { Hours(it).bindOrAccumulate().value }
                    val newContractStartDate = updateDto.contractStartDate ?: existingEmployee.contractPeriod.startDate
                    val newContractEndDate = updateDto.contractEndDate ?: existingEmployee.contractPeriod.endDate

                    val newContractPeriod =
                        ContractPeriod(newContractStartDate, newContractEndDate).bindOrAccumulate().value

                    val updatedEmployee = existingEmployee.copy(
                        name = newName ?: existingEmployee.name,
                        surname = newSurname ?: existingEmployee.surname,
                        defaultBudgetHours = newDefaultBudgetHours ?: existingEmployee.defaultBudgetHours,
                        contractPeriod = newContractPeriod
                    )
                    employeeRepository.update(updatedEmployee).mapLeft { listOf(it.message) }.bind()
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

        // DELETE - DELETE /employees/{id}
        delete<EmployeesResource.Id> { resource ->
            val employeeId = EmployeeId(resource.id)
            employeeRepository.deleteById(employeeId).fold(
                ifLeft = { error -> call.respond(HttpStatusCode.InternalServerError, listOf(error.message)) },
                ifRight = { call.respond(HttpStatusCode.NoContent) },
            )
        }
    }
}

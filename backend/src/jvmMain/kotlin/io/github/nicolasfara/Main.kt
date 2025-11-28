package io.github.nicolasfara

import io.github.nicolasfara.customer.CustomerRepositorySql
import io.github.nicolasfara.customer.CustomerRoutes
import io.github.nicolasfara.customer.CustomerRoutes.customerRoutes
import io.github.nicolasfara.rstcovers.domain.customer.CustomerService
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
//import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.resources.Resources
import io.ktor.server.response.*
import io.ktor.server.routing.*
//import io.ktor.server.sessions.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

fun main() {
    embeddedServer(
        Netty,
        port = 8080, // This is the port on which Ktor is listening
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

fun Application.module()  {
    install(Resources)
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }
//    install(StatusPages)
    install(RequestValidation)

//    val redirects = mutableMapOf<String, String>()
//    install(Sessions) {
//        cookie<UserSession>("user_session")
//    }
//    install(Authentication) {
//        oauth("keycloak") {
//
//        }
//    }
    routing {
        get("/") {
            call.respondText { "hello" }
        }
        customerRoutes(CustomerService(CustomerRepositorySql()))
    }
}

//@Serializable
//data class UserSession(val state: String, val token: String)

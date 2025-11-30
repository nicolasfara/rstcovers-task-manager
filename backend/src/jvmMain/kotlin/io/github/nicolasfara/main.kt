package io.github.nicolasfara

import io.github.nicolasfara.customer.CustomerPostgresRepository
import io.github.nicolasfara.customer.CustomerRoutes.customerRoutes
import io.github.nicolasfara.plugins.configureDatabase
import io.github.nicolasfara.plugins.configureMonitoring
import io.github.nicolasfara.plugins.configureSerialization
import io.github.nicolasfara.plugins.configureValidation
import io.github.nicolasfara.rstcovers.domain.customer.CustomerService
import io.ktor.server.application.*
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.resources.Resources
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main(args: Array<String>) {
    embeddedServer(
        factory = Netty,
        environment =
            applicationEnvironment {
                config = databaseEnvironment
            },
        configure = configureEngine,
        module = {
            module()
        },
    ).start(wait = true)
}

val configureEngine: ApplicationEngine.Configuration.() -> Unit = {
    connector {
        host = "0.0.0.0"
        port = 8080
    }
}

val databaseEnvironment: ApplicationConfig =
    MapApplicationConfig(
        "database.name" to "postgres",
        "database.user" to "postgres",
        "database.password" to "mysecretpassword",
    )

suspend fun Application.module() {
    configureDatabase()
    configureSerialization()
    configureMonitoring()
    configureValidation()

    install(Resources)
//    install(StatusPages)

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
        customerRoutes(CustomerService(CustomerPostgresRepository()))
    }
}

// @Serializable
// data class UserSession(val state: String, val token: String)

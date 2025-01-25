package it.nicolasfarabegoli

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.serialization.Serializable

fun main() {
    embeddedServer(
        Netty,
        port = 8080, // This is the port on which Ktor is listening
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

fun Application.module()  {
    val redirects = mutableMapOf<String, String>()
    install(Sessions) {
        cookie<UserSession>("user_session")
    }
    install(Authentication) {
        oauth("keycloak") {

        }
    }
    routing {
        get("/") {
            call.respondText { "hello" }
        }
    }
}

@Serializable
data class UserSession(val state: String, val token: String)

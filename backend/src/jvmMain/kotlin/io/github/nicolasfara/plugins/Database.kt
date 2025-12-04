package io.github.nicolasfara.plugins

import io.github.nicolasfara.customer.Customers
import io.ktor.server.application.Application
import io.r2dbc.spi.ConnectionFactoryOptions
import io.r2dbc.spi.IsolationLevel
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabaseConfig
import org.jetbrains.exposed.v1.r2dbc.SchemaUtils
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction

suspend fun Application.configureDatabase() {
    val config = environment.config.config("database")
    val dbName = config.property("name").getString()
    val dbUser = config.property("user").getString()
    val dbPassword = config.property("password").getString()

    val database =
        R2dbcDatabase.connect(
            url = "r2dbc:postgresql://localhost:5432/$dbName",
            databaseConfig =
                R2dbcDatabaseConfig {
                    defaultMaxAttempts = 1
                    defaultR2dbcIsolationLevel = IsolationLevel.READ_COMMITTED
                    connectionFactoryOptions {
                        option(ConnectionFactoryOptions.USER, dbUser)
                        option(ConnectionFactoryOptions.PASSWORD, dbPassword)
                    }
                },
        )

    suspendTransaction(db = database) {
        SchemaUtils.drop(Customers)
        SchemaUtils.create(Customers)
    }
}

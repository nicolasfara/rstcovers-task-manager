package io.github.nicolasfara

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.core.StdOutSqlLogger
import org.jetbrains.exposed.v1.r2dbc.R2dbcTransaction
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction

interface PostgresRepository {
    suspend fun <T> dbQuery(block: suspend R2dbcTransaction.() -> T): T =
        withContext(Dispatchers.IO) {
            suspendTransaction {
                addLogger(StdOutSqlLogger)
                block()
            }
        }
}

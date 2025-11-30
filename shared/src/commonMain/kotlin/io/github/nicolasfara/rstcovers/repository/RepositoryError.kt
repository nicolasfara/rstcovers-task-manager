package io.github.nicolasfara.rstcovers.repository

sealed interface RepositoryError {
    data class NotFound<ID>(
        val id: ID,
    ) : RepositoryError

    data class AlreadyExists<ID>(
        val id: ID,
    ) : RepositoryError

    data class Unknown<ID>(
        val id: ID,
    ) : RepositoryError

    data class ConnectionError(
        val message: String,
    ) : RepositoryError

    data class PersistenceError(
        val reason: String,
    ) : RepositoryError

    data class ConstraintViolation(
        val message: String,
    ) : RepositoryError
}

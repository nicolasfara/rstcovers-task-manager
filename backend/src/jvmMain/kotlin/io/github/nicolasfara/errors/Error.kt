package io.github.nicolasfara.errors

import arrow.core.Either
import arrow.core.Nel
import io.github.nicolasfara.rstcovers.domain.errors.Error

fun Throwable?.collectErrors(): List<String> = this?.let {
    listOf(it.message ?: "Unknown error") + this.cause.collectErrors()
}.orEmpty()

fun <E : Error, A> Either<E, A>.getOrThrow(): A =
    when (this) {
        is Either.Left -> error("Data layer corrupted: ${this.value.message}")
        is Either.Right -> this.value
    }

fun <E : Error, A> Either<Nel<E>, A>.getOrThrowAll(): A =
    when (this) {
        is Either.Left -> error("Data layer corrupted: ${this.value.joinToString { it.message }}")
        is Either.Right -> this.value
    }

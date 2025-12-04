package io.github.nicolasfara.errors

fun Throwable?.collectErrors(): List<String> = this?.let {
    listOf(it.message ?: "Unknown error") + this.cause.collectErrors()
} ?: emptyList()

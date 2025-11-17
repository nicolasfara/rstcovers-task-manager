package io.github.nicolasfara.rstcovers.model

import kotlinx.serialization.Serializable

@Serializable
data class Client(val clientCode: Int, val clientName: String)

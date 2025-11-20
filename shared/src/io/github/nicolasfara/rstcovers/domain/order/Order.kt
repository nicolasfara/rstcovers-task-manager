package io.github.nicolasfara.rstcovers.domain.order

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline
import kotlin.uuid.Uuid

@Serializable
@JvmInline
value class OrderId(val id: Uuid = Uuid.random())

data class Order(
    val id: OrderId
)

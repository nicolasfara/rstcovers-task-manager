package io.github.nicolasfara.customer

import io.ktor.resources.Resource
import kotlin.uuid.Uuid

@Resource("/customers")
class CustomersResource(
    val page: Long? = null,
    val pageSize: Int? = null,
) {
    @Resource("/{id}")
    class Id(
        val parent: CustomersResource = CustomersResource(),
        val id: Uuid,
    )
}

package io.github.nicolasfara.employee

import io.ktor.resources.Resource
import kotlin.uuid.Uuid

@Resource("/employees")
class EmployeesResource(
    val page: Long? = null,
    val pageSize: Int? = null,
) {
    @Resource("/{id}")
    class Id(
        val parent: EmployeesResource = EmployeesResource(),
        val id: Uuid,
    )
}

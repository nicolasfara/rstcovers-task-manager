package io.github.nicolasfara.rstcovers.domain.services

import io.github.nicolasfara.rstcovers.domain.aggregates.Employee
import io.github.nicolasfara.rstcovers.domain.aggregates.Order
import io.github.nicolasfara.rstcovers.domain.aggregates.WeeklyPlan

interface RecomputePlanService {
    fun recomputePlan(
        orders: List<Order>,
        employees: List<Employee>,
    ): List<WeeklyPlan>
}

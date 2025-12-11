package io.github.nicolasfara.rstcovers.domain.services

import arrow.core.Either
import arrow.core.Nel
import io.github.nicolasfara.rstcovers.domain.Hours
import io.github.nicolasfara.rstcovers.domain.PlanValidationResult
import io.github.nicolasfara.rstcovers.domain.Priority
import io.github.nicolasfara.rstcovers.domain.Week
import io.github.nicolasfara.rstcovers.domain.aggregates.Employee
import io.github.nicolasfara.rstcovers.domain.aggregates.Order
import io.github.nicolasfara.rstcovers.domain.aggregates.WeeklyPlan
import io.github.nicolasfara.rstcovers.domain.errors.BusinessError
import io.github.nicolasfara.rstcovers.domain.errors.Error
import kotlinx.datetime.LocalDate

interface PlanOrdersService {
    fun calculateWeeklyPlan(
        week: Week,
        orders: Nel<Order>,
        employees: Nel<Employee>,
    ): Either<Error, WeeklyPlan>

    fun validateNewOrder(
        newOrder: Order,
        existingOrders: List<Order>,
        employees: Nel<Employee>,
    ): Either<BusinessError, PlanValidationResult>

    fun calculateFirstAvailableDeliveryDate(
        requiredHours: Hours,
        priority: Priority,
        existingOrders: List<Order>,
        employees: List<Employee>,
    ): LocalDate
}

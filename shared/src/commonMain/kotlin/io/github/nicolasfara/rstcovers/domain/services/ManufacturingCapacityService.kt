package io.github.nicolasfara.rstcovers.domain.services

import io.github.nicolasfara.rstcovers.domain.Hours
import io.github.nicolasfara.rstcovers.domain.Week
import io.github.nicolasfara.rstcovers.domain.aggregates.Employee

interface ManufacturingCapacityService {
    fun computeAvailableManufacturingCapacity(
        week: Week,
        employees: List<Employee>,
    ): Hours

    fun computeAlreadyPlannedWorkload(
        week: Week,
        employees: List<Employee>,
    ): Hours
}
package com.santiago.soberpath.domain.model

import java.time.LocalDate

// Core habit tracked by the app.
data class Habit(
    val id: String,
    val name: String,
    val category: String,
    val startDate: LocalDate,
    val lastRelapseDate: LocalDate,
    val dailyCost: Double,
    val currency: String,
    val isActive: Boolean
)


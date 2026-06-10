package com.santiago.soberpath.domain.model

import java.time.LocalDateTime

// Core habit tracked by the app.
data class Habit(
    val id: String,
    val name: String,
    val category: String,
    val startDate: LocalDateTime,
    val lastRelapseDate: LocalDateTime,
    val dailyCost: Double,
    val currency: String,
    val isActive: Boolean
)


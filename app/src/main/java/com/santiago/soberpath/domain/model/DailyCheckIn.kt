package com.santiago.soberpath.domain.model

import java.time.LocalDate

// Daily pledge and emotional log.
data class DailyCheckIn(
    val id: String,
    val habitId: String,
    val date: LocalDate,
    val mood: String,
    val cravingLevel: Int,
    val note: String?,
    val completedPledge: Boolean
)


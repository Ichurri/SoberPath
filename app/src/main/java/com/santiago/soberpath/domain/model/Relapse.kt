package com.santiago.soberpath.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

data class Relapse(
    val id: String,
    val habitId: String,
    val relapseDate: LocalDate,
    val cravingLevel: Int,
    val trigger: String,
    val note: String,
    val createdAt: LocalDateTime
)
package com.santiago.soberpath.domain.model

import java.time.LocalDateTime

// Personal motivation to stay on track.
data class MotivationReason(
    val id: String,
    val habitId: String,
    val text: String,
    val createdAt: LocalDateTime
)


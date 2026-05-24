package com.santiago.soberpath.domain.model

// Snapshot of current progress since last relapse.
data class SobrietyProgress(
    val totalMinutesSinceRelapse: Long,
    val days: Long,
    val hours: Long,
    val minutes: Long,
    val savedAmount: Double
)


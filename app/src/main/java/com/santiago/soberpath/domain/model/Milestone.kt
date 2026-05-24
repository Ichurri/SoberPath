package com.santiago.soberpath.domain.model

// Milestone based on days without relapse.
data class Milestone(
    val id: String,
    val title: String,
    val daysRequired: Int,
    val achieved: Boolean
)


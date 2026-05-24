package com.santiago.soberpath.presentation.screen.home

data class HomePreviewState(
    val timeSinceRelapse: String,
    val savings: String,
    val motivationalMessage: String
)

object HomePreviewData {
    val sample = HomePreviewState(
        timeSinceRelapse = "12d 04h 18m",
        savings = "$48.00",
        motivationalMessage = "You are building momentum."
    )
}

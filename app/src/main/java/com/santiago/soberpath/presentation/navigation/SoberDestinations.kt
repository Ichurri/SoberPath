package com.santiago.soberpath.presentation.navigation

sealed class SoberDestination(val route: String) {
    object Onboarding : SoberDestination("onboarding")
    object Home : SoberDestination("home")
    object DailyCheckIn : SoberDestination("daily_check_in")
    object Motivation : SoberDestination("motivation")
    object Milestones : SoberDestination("milestones")
    object Settings : SoberDestination("settings")
}


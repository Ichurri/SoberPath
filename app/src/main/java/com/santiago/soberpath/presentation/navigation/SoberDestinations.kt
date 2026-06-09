package com.santiago.soberpath.presentation.navigation

sealed class SoberDestination(val route: String) {
    object Splash : SoberDestination("splash")
    object Onboarding : SoberDestination("onboarding")
    object RecoverySetup: SoberDestination("recovery_setup")
    object Home : SoberDestination("home")
    object DailyCheckIn : SoberDestination("daily_check_in")
    object Motivation : SoberDestination("motivation")
    object Milestones : SoberDestination("milestones")
    object Settings : SoberDestination("settings")
    object RegisterRelapse : SoberDestination("register_relapse")
    object RelapseHistory : SoberDestination("relapse_history")
}


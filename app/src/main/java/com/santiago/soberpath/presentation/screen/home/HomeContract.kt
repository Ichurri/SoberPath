package com.santiago.soberpath.presentation.screen.home

import com.santiago.soberpath.presentation.util.UiText

object HomeContract {

    data class UiState(
        val isLoading: Boolean = true,
        val hasHabit: Boolean = false,
        val habitName: String = "",
        val timeSinceRelapse: String = "",
        val savingsText: String = "",
        val motivationalMessage: String = "",
        val emergencyTipsEnabled: Boolean = false,
        val emergencyTipsMessage: String = "",
        val recentCheckIns: List<CheckInUi> = emptyList(),
        val relapseCount: Int = 0,
        val lastRelapseDate: String = ""
    )

    data class CheckInUi(
        val date: String,
        val mood: String,
        val cravingLevel: Int
    )

    sealed interface UiIntent {
        object DailyCheckInClicked : UiIntent
        object RegisterRelapseClicked : UiIntent
        object RelapseHistoryClicked : UiIntent
        object MotivationClicked : UiIntent
        object MilestonesClicked : UiIntent
        object SettingsClicked : UiIntent
        object SetupRecoveryClicked : UiIntent
        object HabitsClicked : UiIntent
    }

    sealed interface UiEffect {
        object NavigateDailyCheckIn : UiEffect
        object NavigateHabits : UiEffect
        object NavigateMotivation : UiEffect
        object NavigateMilestones : UiEffect
        object NavigateSettings : UiEffect
        object NavigateRegisterRelapse : UiEffect
        object NavigateRelapseHistory : UiEffect
        object NavigateRecoverySetup : UiEffect
        data class ShowMessage(val message: UiText) : UiEffect
    }
}
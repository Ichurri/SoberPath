package com.santiago.soberpath.presentation.screen.home

import com.santiago.soberpath.presentation.util.UiText

object HomeContract {
    data class UiState(
        val isLoading: Boolean = true,
        val hasHabit: Boolean = false,
        val habitName: String = "",
        val timeSinceRelapse: String = "",
        val savingsText: String = "",
        val motivationalMessage: String = ""
    )

    sealed interface UiIntent {
        object DailyCheckInClicked : UiIntent
        object RegisterRelapseClicked : UiIntent
        object MotivationClicked : UiIntent
        object MilestonesClicked : UiIntent
        object SettingsClicked : UiIntent
    }

    sealed interface UiEffect {
        object NavigateDailyCheckIn : UiEffect
        object NavigateMotivation : UiEffect
        object NavigateMilestones : UiEffect
        object NavigateSettings : UiEffect
        data class ShowMessage(val message: UiText) : UiEffect
    }
}


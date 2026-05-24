package com.santiago.soberpath.presentation.screen.onboarding

import com.santiago.soberpath.presentation.util.UiText

object OnboardingContract {
    data class UiState(
        val habitName: String = "",
        val category: String = "",
        val startDate: String = "",
        val dailyCost: String = "",
        val currency: String = "",
        val isLoading: Boolean = false
    )

    sealed interface UiIntent {
        data class UpdateHabitName(val value: String) : UiIntent
        data class UpdateCategory(val value: String) : UiIntent
        data class UpdateStartDate(val value: String) : UiIntent
        data class UpdateDailyCost(val value: String) : UiIntent
        data class UpdateCurrency(val value: String) : UiIntent
        object Submit : UiIntent
    }

    sealed interface UiEffect {
        object NavigateHome : UiEffect
        data class ShowMessage(val message: UiText) : UiEffect
    }
}


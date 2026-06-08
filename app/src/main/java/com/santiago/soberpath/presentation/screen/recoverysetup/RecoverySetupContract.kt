package com.santiago.soberpath.presentation.screen.recoverysetup

import com.santiago.soberpath.presentation.util.UiText

object RecoverySetupContract {

    data class UiState(
        val habitName: String = "",
        val dailyCost: String = "",
        val currency: String = "Bs",
        val isSaving: Boolean = false,
        val nameError: UiText? = null,
        val costError: UiText? = null
    )

    sealed interface UiIntent {
        data class HabitNameChanged(val value: String) : UiIntent
        data class DailyCostChanged(val value: String) : UiIntent
        data class CurrencyChanged(val value: String) : UiIntent
        object SaveClicked : UiIntent
    }

    sealed interface UiEffect {
        object NavigateHome : UiEffect
        data class ShowMessage(val message: UiText) : UiEffect
    }
}
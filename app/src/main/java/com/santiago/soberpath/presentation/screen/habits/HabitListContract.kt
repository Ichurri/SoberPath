package com.santiago.soberpath.presentation.screen.habits

import com.santiago.soberpath.presentation.util.UiText

object HabitListContract {

    data class UiState(
        val isLoading: Boolean = true,
        val habits: List<HabitUi> = emptyList()
    )

    data class HabitUi(
        val id: String,
        val name: String,
        val category: String,
        val startDate: String,
        val isActive: Boolean
    )

    sealed interface UiIntent {
        data class SelectHabit(val habitId: String) : UiIntent
        data class DeleteHabit(val habitId: String) : UiIntent
        object CreateNewHabit : UiIntent
        object Back : UiIntent
    }

    sealed interface UiEffect {
        object NavigateBack : UiEffect
        object NavigateRecoverySetup : UiEffect
        data class ShowMessage(val message: UiText) : UiEffect
    }
}
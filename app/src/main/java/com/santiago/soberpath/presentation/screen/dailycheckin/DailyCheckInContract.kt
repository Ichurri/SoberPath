package com.santiago.soberpath.presentation.screen.dailycheckin

import com.santiago.soberpath.presentation.util.UiText

object DailyCheckInContract {
    data class UiState(
        val mood: String = "",
        val cravingLevel: Int = 3,
        val note: String = "",
        val pledgeChecked: Boolean = false,
        val isSaving: Boolean = false
    )

    sealed interface UiIntent {
        data class UpdateMood(val value: String) : UiIntent
        data class UpdateCraving(val value: Int) : UiIntent
        data class UpdateNote(val value: String) : UiIntent
        data class UpdatePledge(val value: Boolean) : UiIntent
        object Save : UiIntent
    }

    sealed interface UiEffect {
        object NavigateBack : UiEffect
        data class ShowMessage(val message: UiText) : UiEffect
    }
}


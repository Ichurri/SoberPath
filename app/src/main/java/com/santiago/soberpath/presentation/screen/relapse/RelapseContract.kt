package com.santiago.soberpath.presentation.screen.relapse

import com.santiago.soberpath.presentation.util.UiText
import java.time.LocalDate

class RelapseContract {
    data class UiState(
        val isLoading: Boolean = false,
        val habitId: String = "",
        val date: LocalDate = LocalDate.now(),
        val cravingLevel: Int = 5,
        val trigger: String = "",
        val note: String = "",
        val isSaving: Boolean = false
    )

    sealed class UiIntent {
        data class DateChanged(val date: LocalDate) : UiIntent()
        data class CravingLevelChanged(val level: Int) : UiIntent()
        data class TriggerChanged(val trigger: String) : UiIntent()
        data class NoteChanged(val note: String) : UiIntent()
        object SaveClicked : UiIntent()
        object BackClicked : UiIntent()
    }

    sealed class UiEffect {
        object NavigateBack : UiEffect()
        data class ShowMessage(val message: UiText) : UiEffect()
    }
}

package com.santiago.soberpath.presentation.screen.motivation

import com.santiago.soberpath.presentation.util.UiText

object MotivationContract {
    data class UiState(
        val reasons: List<String> = emptyList(),
        val newReason: String = "",
        val isLoading: Boolean = false
    )

    sealed interface UiIntent {
        data class UpdateNewReason(val value: String) : UiIntent
        object AddReason : UiIntent
        object Back : UiIntent
    }

    sealed interface UiEffect {
        object NavigateBack : UiEffect
        data class ShowMessage(val message: UiText) : UiEffect
    }
}


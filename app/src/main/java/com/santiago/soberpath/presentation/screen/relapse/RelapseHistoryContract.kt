package com.santiago.soberpath.presentation.screen.relapse

import com.santiago.soberpath.domain.model.Relapse

class RelapseHistoryContract {
    data class UiState(
        val isLoading: Boolean = true,
        val relapses: List<Relapse> = emptyList()
    )

    sealed interface UiIntent {
        object BackClicked : UiIntent
    }

    sealed interface UiEffect {
        object NavigateBack : UiEffect
    }
}

package com.santiago.soberpath.presentation.screen.milestones

import com.santiago.soberpath.presentation.util.UiText

object MilestonesContract {
    data class UiState(
        val milestones: List<MilestoneUi> = emptyList(),
        val isLoading: Boolean = false
    )

    data class MilestoneUi(
        val title: String,
        val daysRequired: Int,
        val achieved: Boolean
    )

    sealed interface UiIntent {
        object Back : UiIntent
    }

    sealed interface UiEffect {
        object NavigateBack : UiEffect
        data class ShowMessage(val message: UiText) : UiEffect
    }
}


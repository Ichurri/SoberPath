package com.santiago.soberpath.presentation.screen.relapsehistory

import com.santiago.soberpath.presentation.util.UiText
import java.time.LocalDate

object RelapseHistoryContract {

    data class UiState(
        val isLoading: Boolean = true,
        val hasHabit: Boolean = false,
        val activeHabitName: String = "",
        val relapses: List<RelapseUi> = emptyList()
    )

    data class RelapseUi(
        val id: String,
        val date: String,
        val cravingLevel: Int,
        val trigger: String,
        val note: String
    )

    sealed interface UiIntent {
        object Back : UiIntent
        data class AddRelapse(
            val date: LocalDate,
            val cravingLevel: Int,
            val trigger: String,
            val note: String
        ) : UiIntent
    }

    sealed interface UiEffect {
        object NavigateBack : UiEffect
        data class ShowMessage(val message: UiText) : UiEffect
    }
}

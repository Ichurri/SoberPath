package com.santiago.soberpath.presentation.screen.relapse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.santiago.soberpath.domain.usecase.GetActiveHabitUseCase
import com.santiago.soberpath.domain.usecase.GetRelapsesUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RelapseHistoryViewModel(
    private val getActiveHabitUseCase: GetActiveHabitUseCase,
    private val getRelapsesUseCase: GetRelapsesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RelapseHistoryContract.UiState())
    val state: StateFlow<RelapseHistoryContract.UiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<RelapseHistoryContract.UiEffect>()
    val effect = _effect.asSharedFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            val habit = getActiveHabitUseCase().firstOrNull() ?: return@launch
            getRelapsesUseCase(habit.id).collectLatest { relapses ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        relapses = relapses
                    )
                }
            }
        }
    }

    fun onIntent(intent: RelapseHistoryContract.UiIntent) {
        when (intent) {
            RelapseHistoryContract.UiIntent.BackClicked -> {
                viewModelScope.launch {
                    _effect.emit(RelapseHistoryContract.UiEffect.NavigateBack)
                }
            }
        }
    }
}

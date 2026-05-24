package com.santiago.soberpath.presentation.screen.milestones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.santiago.soberpath.R
import com.santiago.soberpath.domain.usecase.GetActiveHabitUseCase
import com.santiago.soberpath.domain.usecase.GetMilestonesUseCase
import com.santiago.soberpath.presentation.util.UiText
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MilestonesViewModel(
    private val getActiveHabitUseCase: GetActiveHabitUseCase,
    private val getMilestonesUseCase: GetMilestonesUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(MilestonesContract.UiState())
    val state: StateFlow<MilestonesContract.UiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<MilestonesContract.UiEffect>()
    val effect = _effect.asSharedFlow()

    private var milestonesJob: Job? = null

    init {
        viewModelScope.launch {
            getActiveHabitUseCase().collectLatest { habit ->
                if (habit == null) {
                    milestonesJob?.cancel()
                    _state.update { it.copy(milestones = emptyList(), isLoading = false) }
                } else {
                    observeMilestones(habit.id)
                }
            }
        }
    }

    fun onIntent(intent: MilestonesContract.UiIntent) {
        when (intent) {
            MilestonesContract.UiIntent.Back -> emitEffect(MilestonesContract.UiEffect.NavigateBack)
        }
    }

    private fun observeMilestones(habitId: String) {
        milestonesJob?.cancel()
        milestonesJob = viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getMilestonesUseCase(habitId).collectLatest { list ->
                val uiList = list.map {
                    MilestonesContract.MilestoneUi(
                        title = it.title,
                        daysRequired = it.daysRequired,
                        achieved = it.achieved
                    )
                }
                _state.update { it.copy(milestones = uiList, isLoading = false) }
            }
        }
    }

    private fun emitEffect(effect: MilestonesContract.UiEffect) {
        viewModelScope.launch { _effect.emit(effect) }
    }

    private fun emitMessage(messageRes: Int) {
        viewModelScope.launch {
            _effect.emit(
                MilestonesContract.UiEffect.ShowMessage(UiText.StringResource(messageRes))
            )
        }
    }
}


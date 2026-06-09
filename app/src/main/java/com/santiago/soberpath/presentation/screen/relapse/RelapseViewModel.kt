package com.santiago.soberpath.presentation.screen.relapse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.santiago.soberpath.R
import com.santiago.soberpath.domain.usecase.GetActiveHabitUseCase
import com.santiago.soberpath.domain.usecase.RegisterRelapseUseCase
import com.santiago.soberpath.presentation.util.UiText
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RelapseViewModel(
    private val getActiveHabitUseCase: GetActiveHabitUseCase,
    private val registerRelapseUseCase: RegisterRelapseUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RelapseContract.UiState())
    val state: StateFlow<RelapseContract.UiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<RelapseContract.UiEffect>()
    val effect = _effect.asSharedFlow()

    init {
        loadHabit()
    }

    private fun loadHabit() {
        viewModelScope.launch {
            val habit = getActiveHabitUseCase().firstOrNull()
            if (habit != null) {
                _state.update { it.copy(habitId = habit.id) }
            } else {
                emitEffect(RelapseContract.UiEffect.ShowMessage(UiText.StringResource(R.string.message_no_active_habit)))
                emitEffect(RelapseContract.UiEffect.NavigateBack)
            }
        }
    }

    fun onIntent(intent: RelapseContract.UiIntent) {
        when (intent) {
            is RelapseContract.UiIntent.DateChanged -> {
                _state.update { it.copy(date = intent.date) }
            }
            is RelapseContract.UiIntent.CravingLevelChanged -> {
                _state.update { it.copy(cravingLevel = intent.level) }
            }
            is RelapseContract.UiIntent.TriggerChanged -> {
                _state.update { it.copy(trigger = intent.trigger) }
            }
            is RelapseContract.UiIntent.NoteChanged -> {
                _state.update { it.copy(note = intent.note) }
            }
            RelapseContract.UiIntent.SaveClicked -> {
                registerRelapse()
            }
            RelapseContract.UiIntent.BackClicked -> {
                emitEffect(RelapseContract.UiEffect.NavigateBack)
            }
        }
    }

    private fun registerRelapse() {
        val currentState = state.value
        if (currentState.habitId.isBlank()) return

        _state.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            runCatching {
                registerRelapseUseCase(
                    habitId = currentState.habitId,
                    relapseDate = currentState.date,
                    cravingLevel = currentState.cravingLevel,
                    trigger = currentState.trigger,
                    note = currentState.note
                )
            }.onSuccess {
                emitEffect(RelapseContract.UiEffect.ShowMessage(UiText.StringResource(R.string.message_relapse_registered)))
                emitEffect(RelapseContract.UiEffect.NavigateBack)
            }.onFailure {
                _state.update { it.copy(isSaving = false) }
                emitEffect(RelapseContract.UiEffect.ShowMessage(UiText.StringResource(R.string.error_generic)))
            }
        }
    }

    private fun emitEffect(effect: RelapseContract.UiEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }
}

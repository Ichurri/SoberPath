package com.santiago.soberpath.presentation.screen.motivation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.santiago.soberpath.R
import com.santiago.soberpath.domain.model.MotivationReason
import com.santiago.soberpath.domain.usecase.AddMotivationReasonUseCase
import com.santiago.soberpath.domain.usecase.GetActiveHabitUseCase
import com.santiago.soberpath.domain.usecase.GetMotivationReasonsUseCase
import com.santiago.soberpath.presentation.util.UiText
import java.time.LocalDateTime
import java.util.UUID
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MotivationViewModel(
    private val getActiveHabitUseCase: GetActiveHabitUseCase,
    private val getMotivationReasonsUseCase: GetMotivationReasonsUseCase,
    private val addMotivationReasonUseCase: AddMotivationReasonUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(MotivationContract.UiState())
    val state: StateFlow<MotivationContract.UiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<MotivationContract.UiEffect>()
    val effect = _effect.asSharedFlow()

    private var habitId: String? = null
    private var reasonsJob: Job? = null

    init {
        viewModelScope.launch {
            getActiveHabitUseCase().collectLatest { habit ->
                habitId = habit?.id
                if (habit == null) {
                    reasonsJob?.cancel()
                    _state.update { it.copy(reasons = emptyList()) }
                } else {
                    observeReasons(habit.id)
                }
            }
        }
    }

    fun onIntent(intent: MotivationContract.UiIntent) {
        when (intent) {
            is MotivationContract.UiIntent.UpdateNewReason ->
                _state.update { it.copy(newReason = intent.value) }
            MotivationContract.UiIntent.AddReason -> addReason()
            MotivationContract.UiIntent.Back -> emitEffect(MotivationContract.UiEffect.NavigateBack)
        }
    }

    private fun observeReasons(currentHabitId: String) {
        reasonsJob?.cancel()
        reasonsJob = viewModelScope.launch {
            getMotivationReasonsUseCase(currentHabitId).collectLatest { list ->
                _state.update { it.copy(reasons = list.map(MotivationReason::text)) }
            }
        }
    }

    private fun addReason() {
        val current = state.value
        val currentHabitId = habitId
        if (currentHabitId == null) {
            emitMessage(R.string.message_no_active_habit)
            return
        }
        if (current.newReason.isBlank()) {
            emitMessage(R.string.error_required_fields)
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val reason = MotivationReason(
                id = UUID.randomUUID().toString(),
                habitId = currentHabitId,
                text = current.newReason.trim(),
                createdAt = LocalDateTime.now()
            )
            runCatching { addMotivationReasonUseCase(reason) }
                .onSuccess {
                    _state.update { it.copy(newReason = "") }
                    emitMessage(R.string.message_reason_added)
                }
                .onFailure { emitMessage(R.string.error_generic) }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun emitMessage(messageRes: Int) {
        viewModelScope.launch {
            _effect.emit(
                MotivationContract.UiEffect.ShowMessage(UiText.StringResource(messageRes))
            )
        }
    }

    private fun emitEffect(effect: MotivationContract.UiEffect) {
        viewModelScope.launch { _effect.emit(effect) }
    }
}


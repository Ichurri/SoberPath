package com.santiago.soberpath.presentation.screen.dailycheckin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.santiago.soberpath.R
import com.santiago.soberpath.domain.model.DailyCheckIn
import com.santiago.soberpath.domain.usecase.GetActiveHabitUseCase
import com.santiago.soberpath.domain.usecase.SaveDailyCheckInUseCase
import com.santiago.soberpath.presentation.util.UiText
import java.time.LocalDate
import java.util.UUID
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DailyCheckInViewModel(
    private val getActiveHabitUseCase: GetActiveHabitUseCase,
    private val saveDailyCheckInUseCase: SaveDailyCheckInUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(DailyCheckInContract.UiState())
    val state: StateFlow<DailyCheckInContract.UiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<DailyCheckInContract.UiEffect>()
    val effect = _effect.asSharedFlow()

    private var habitId: String? = null

    init {
        viewModelScope.launch {
            getActiveHabitUseCase().collectLatest { habit ->
                habitId = habit?.id
            }
        }
    }

    fun onIntent(intent: DailyCheckInContract.UiIntent) {
        when (intent) {
            is DailyCheckInContract.UiIntent.UpdateMood ->
                _state.update { it.copy(mood = intent.value) }
            is DailyCheckInContract.UiIntent.UpdateCraving ->
                _state.update { it.copy(cravingLevel = intent.value) }
            is DailyCheckInContract.UiIntent.UpdateNote ->
                _state.update { it.copy(note = intent.value) }
            is DailyCheckInContract.UiIntent.UpdatePledge ->
                _state.update { it.copy(pledgeChecked = intent.value) }
            DailyCheckInContract.UiIntent.Save -> saveCheckIn()
        }
    }

    private fun saveCheckIn() {
        val current = state.value
        val currentHabitId = habitId
        if (currentHabitId == null) {
            emitMessage(R.string.message_no_active_habit)
            return
        }
        if (current.mood.isBlank()) {
            emitMessage(R.string.error_required_fields)
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            val checkIn = DailyCheckIn(
                id = UUID.randomUUID().toString(),
                habitId = currentHabitId,
                date = LocalDate.now(),
                mood = current.mood.trim(),
                cravingLevel = current.cravingLevel,
                note = current.note.trim().ifBlank { null },
                completedPledge = current.pledgeChecked
            )
            runCatching { saveDailyCheckInUseCase(checkIn) }
                .onSuccess {
                    _effect.emit(DailyCheckInContract.UiEffect.NavigateBack)
                }
                .onFailure { emitMessage(R.string.error_generic) }
            _state.update { it.copy(isSaving = false) }
        }
    }

    private fun emitMessage(messageRes: Int) {
        viewModelScope.launch {
            _effect.emit(
                DailyCheckInContract.UiEffect.ShowMessage(UiText.StringResource(messageRes))
            )
        }
    }
}


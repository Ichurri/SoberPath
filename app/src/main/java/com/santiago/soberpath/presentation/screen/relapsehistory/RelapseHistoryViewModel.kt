package com.santiago.soberpath.presentation.screen.relapsehistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.santiago.soberpath.R
import com.santiago.soberpath.domain.usecase.GetActiveHabitUseCase
import com.santiago.soberpath.domain.usecase.GetRelapsesUseCase
import com.santiago.soberpath.domain.usecase.RegisterRelapseUseCase
import com.santiago.soberpath.presentation.util.UiText
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RelapseHistoryViewModel(
    private val getActiveHabitUseCase: GetActiveHabitUseCase,
    private val getRelapsesUseCase: GetRelapsesUseCase,
    private val registerRelapseUseCase: RegisterRelapseUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RelapseHistoryContract.UiState())
    val state: StateFlow<RelapseHistoryContract.UiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<RelapseHistoryContract.UiEffect>()
    val effect = _effect.asSharedFlow()

    private var relapsesJob: Job? = null
    private var activeHabitId: String? = null

    init {
        observeActiveHabit()
    }

    fun onIntent(intent: RelapseHistoryContract.UiIntent) {
        when (intent) {
            RelapseHistoryContract.UiIntent.Back -> {
                emitEffect(RelapseHistoryContract.UiEffect.NavigateBack)
            }
            is RelapseHistoryContract.UiIntent.AddRelapse -> {
                addRelapse(intent.date, intent.cravingLevel, intent.trigger, intent.note)
            }
        }
    }

    private fun observeActiveHabit() {
        viewModelScope.launch {
            getActiveHabitUseCase().collectLatest { habit ->
                if (habit == null) {
                    relapsesJob?.cancel()
                    _state.update {
                        it.copy(
                            isLoading = false,
                            hasHabit = false,
                            activeHabitName = "",
                            relapses = emptyList()
                        )
                    }
                } else {
                    activeHabitId = habit.id
                    _state.update {
                        it.copy(
                            hasHabit = true,
                            activeHabitName = habit.name
                        )
                    }
                    observeRelapses(habit.id)
                }
            }
        }
    }

    private fun observeRelapses(habitId: String) {
        relapsesJob?.cancel()
        relapsesJob = viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getRelapsesUseCase(habitId).collectLatest { list ->
                val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())
                val uiList = list.map {
                    RelapseHistoryContract.RelapseUi(
                        id = it.id,
                        date = it.relapseDate.format(formatter),
                        cravingLevel = it.cravingLevel,
                        trigger = it.trigger,
                        note = it.note
                    )
                }
                _state.update {
                    it.copy(
                        isLoading = false,
                        relapses = uiList
                    )
                }
            }
        }
    }

    private fun addRelapse(date: LocalDate, cravingLevel: Int, trigger: String, note: String) {
        val habitId = activeHabitId
        if (habitId == null) {
            emitMessage(R.string.message_no_active_habit)
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            runCatching {
                registerRelapseUseCase(
                    habitId = habitId,
                    relapseDate = date,
                    cravingLevel = cravingLevel,
                    trigger = trigger.trim(),
                    note = note.trim()
                )
            }.onSuccess {
                _state.update { it.copy(isLoading = false) }
                emitMessage(R.string.message_relapse_registered)
            }.onFailure {
                _state.update { it.copy(isLoading = false) }
                emitMessage(R.string.error_generic)
            }
        }
    }

    private fun emitEffect(effect: RelapseHistoryContract.UiEffect) {
        viewModelScope.launch { _effect.emit(effect) }
    }

    private fun emitMessage(messageRes: Int) {
        viewModelScope.launch {
            _effect.emit(
                RelapseHistoryContract.UiEffect.ShowMessage(UiText.StringResource(messageRes))
            )
        }
    }
}

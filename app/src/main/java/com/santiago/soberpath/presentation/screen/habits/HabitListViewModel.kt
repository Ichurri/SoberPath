package com.santiago.soberpath.presentation.screen.habits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.santiago.soberpath.R
import com.santiago.soberpath.domain.usecase.GetAllHabitsUseCase
import com.santiago.soberpath.domain.usecase.SetActiveHabitUseCase
import com.santiago.soberpath.domain.usecase.DeleteHabitUseCase
import com.santiago.soberpath.presentation.util.DateFormatters
import com.santiago.soberpath.presentation.util.UiText
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HabitListViewModel(
    private val getAllHabitsUseCase: GetAllHabitsUseCase,
    private val setActiveHabitUseCase: SetActiveHabitUseCase,
    private val deleteHabitUseCase: DeleteHabitUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HabitListContract.UiState())
    val state: StateFlow<HabitListContract.UiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<HabitListContract.UiEffect>()
    val effect = _effect.asSharedFlow()

    init {
        observeHabits()
    }

    fun onIntent(intent: HabitListContract.UiIntent) {
        when (intent) {
            HabitListContract.UiIntent.Back -> {
                emitEffect(HabitListContract.UiEffect.NavigateBack)
            }

            HabitListContract.UiIntent.CreateNewHabit -> {
                emitEffect(HabitListContract.UiEffect.NavigateRecoverySetup)
            }

            is HabitListContract.UiIntent.SelectHabit -> {
                selectHabit(intent.habitId)
            }

            is HabitListContract.UiIntent.DeleteHabit -> {
                deleteHabit(intent.habitId)
            }
        }
    }

    private fun observeHabits() {
        viewModelScope.launch {
            getAllHabitsUseCase().collectLatest { habits ->
                val items = habits.map { habit ->
                    HabitListContract.HabitUi(
                        id = habit.id,
                        name = habit.name,
                        category = habit.category,
                        startDate = DateFormatters.mediumDate(habit.startDate),
                        isActive = habit.isActive
                    )
                }

                _state.update {
                    it.copy(
                        isLoading = false,
                        habits = items
                    )
                }
            }
        }
    }

    private fun selectHabit(habitId: String) {
        viewModelScope.launch {
            runCatching {
                setActiveHabitUseCase(habitId)
            }.onSuccess {
                _effect.emit(
                    HabitListContract.UiEffect.ShowMessage(
                        UiText.StringResource(R.string.habits_selected_message)
                    )
                )
                _effect.emit(HabitListContract.UiEffect.NavigateBack)
            }.onFailure {
                _effect.emit(
                    HabitListContract.UiEffect.ShowMessage(
                        UiText.StringResource(R.string.error_generic)
                    )
                )
            }
        }
    }

    private fun deleteHabit(habitId: String) {
        viewModelScope.launch {
            runCatching {
                deleteHabitUseCase(habitId)
            }.onSuccess {
                _effect.emit(
                    HabitListContract.UiEffect.ShowMessage(
                        UiText.StringResource(R.string.habits_deleted_message)
                    )
                )
            }.onFailure {
                _effect.emit(
                    HabitListContract.UiEffect.ShowMessage(
                        UiText.StringResource(R.string.error_generic)
                    )
                )
            }
        }
    }

    private fun emitEffect(effect: HabitListContract.UiEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }
}
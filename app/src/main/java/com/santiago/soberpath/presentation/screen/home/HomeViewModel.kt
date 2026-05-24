package com.santiago.soberpath.presentation.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.santiago.soberpath.R
import com.santiago.soberpath.domain.model.Habit
import com.santiago.soberpath.domain.model.SobrietyProgress
import com.santiago.soberpath.domain.usecase.GetActiveHabitUseCase
import com.santiago.soberpath.domain.usecase.GetSobrietyProgressUseCase
import com.santiago.soberpath.presentation.util.UiText
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

class HomeViewModel(
    private val getActiveHabitUseCase: GetActiveHabitUseCase,
    private val getSobrietyProgressUseCase: GetSobrietyProgressUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(HomeContract.UiState())
    val state: StateFlow<HomeContract.UiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<HomeContract.UiEffect>()
    val effect = _effect.asSharedFlow()

    private var progressJob: Job? = null

    init {
        observeHabit()
    }

    fun onIntent(intent: HomeContract.UiIntent) {
        when (intent) {
            HomeContract.UiIntent.DailyCheckInClicked -> emitEffect(HomeContract.UiEffect.NavigateDailyCheckIn)
            HomeContract.UiIntent.MotivationClicked -> emitEffect(HomeContract.UiEffect.NavigateMotivation)
            HomeContract.UiIntent.MilestonesClicked -> emitEffect(HomeContract.UiEffect.NavigateMilestones)
            HomeContract.UiIntent.SettingsClicked -> emitEffect(HomeContract.UiEffect.NavigateSettings)
            HomeContract.UiIntent.RegisterRelapseClicked -> emitEffect(
                HomeContract.UiEffect.ShowMessage(UiText.StringResource(R.string.message_relapse_not_ready))
            )
        }
    }

    private fun observeHabit() {
        viewModelScope.launch {
            getActiveHabitUseCase().collectLatest { habit ->
                if (habit == null) {
                    progressJob?.cancel()
                    _state.update {
                        it.copy(
                            isLoading = false,
                            hasHabit = false,
                            habitName = "",
                            timeSinceRelapse = "",
                            savingsText = "",
                            motivationalMessage = ""
                        )
                    }
                } else {
                    _state.update { it.copy(isLoading = false, hasHabit = true, habitName = habit.name) }
                    observeProgress(habit)
                }
            }
        }
    }

    private fun observeProgress(habit: Habit) {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            getSobrietyProgressUseCase(habit.id).collectLatest { progress ->
                _state.update {
                    it.copy(
                        timeSinceRelapse = formatDuration(progress),
                        savingsText = formatSavings(habit.currency, progress),
                        motivationalMessage = ""
                    )
                }
            }
        }
    }

    private fun formatDuration(progress: SobrietyProgress): String {
        return "${progress.days}d ${progress.hours}h ${progress.minutes}m"
    }

    private fun formatSavings(currency: String, progress: SobrietyProgress): String {
        val formatted = String.format(Locale.getDefault(), "%.2f", progress.savedAmount)
        return "$currency$formatted"
    }

    private fun emitEffect(effect: HomeContract.UiEffect) {
        viewModelScope.launch { _effect.emit(effect) }
    }
}


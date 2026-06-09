package com.santiago.soberpath.presentation.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.santiago.soberpath.domain.model.Habit
import com.santiago.soberpath.domain.model.SobrietyProgress
import com.santiago.soberpath.domain.usecase.GetActiveHabitUseCase
import com.santiago.soberpath.domain.usecase.GetDailyCheckInsUseCase
import com.santiago.soberpath.domain.usecase.GetRelapsesUseCase
import com.santiago.soberpath.domain.usecase.GetRemoteConfigUseCase
import com.santiago.soberpath.domain.usecase.GetSobrietyProgressUseCase
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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getActiveHabitUseCase: GetActiveHabitUseCase,
    private val getSobrietyProgressUseCase: GetSobrietyProgressUseCase,
    private val getRemoteConfigUseCase: GetRemoteConfigUseCase,
    private val getDailyCheckInsUseCase: GetDailyCheckInsUseCase,
    private val getRelapsesUseCase: GetRelapsesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeContract.UiState())
    val state: StateFlow<HomeContract.UiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<HomeContract.UiEffect>()
    val effect = _effect.asSharedFlow()

    private var progressJob: Job? = null
    private var checkInsJob: Job? = null
    private var relapsesJob: Job? = null

    init {
        observeHabit()
        observeRemoteConfig()
    }

    fun onIntent(intent: HomeContract.UiIntent) {
        when (intent) {
            HomeContract.UiIntent.DailyCheckInClicked -> {
                emitEffect(HomeContract.UiEffect.NavigateDailyCheckIn)
            }

            HomeContract.UiIntent.MotivationClicked -> {
                emitEffect(HomeContract.UiEffect.NavigateMotivation)
            }

            HomeContract.UiIntent.MilestonesClicked -> {
                emitEffect(HomeContract.UiEffect.NavigateMilestones)
            }

            HomeContract.UiIntent.SettingsClicked -> {
                emitEffect(HomeContract.UiEffect.NavigateSettings)
            }

            HomeContract.UiIntent.RegisterRelapseClicked -> {
                emitEffect(HomeContract.UiEffect.NavigateRegisterRelapse)
            }

            HomeContract.UiIntent.RelapseHistoryClicked -> {
                emitEffect(HomeContract.UiEffect.NavigateRelapseHistory)
            }

            HomeContract.UiIntent.SetupRecoveryClicked -> {
                emitEffect(HomeContract.UiEffect.NavigateRecoverySetup)
            }
        }
    }

    private fun observeHabit() {
        viewModelScope.launch {
            getActiveHabitUseCase().collectLatest { habit ->
                if (habit == null) {
                    progressJob?.cancel()
                    checkInsJob?.cancel()
                    relapsesJob?.cancel()

                    _state.update {
                        it.copy(
                            isLoading = false,
                            hasHabit = false,
                            habitName = "",
                            timeSinceRelapse = "",
                            savingsText = "",
                            recentCheckIns = emptyList(),
                            relapseCount = 0,
                            lastRelapseDate = ""
                        )
                    }
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            hasHabit = true,
                            habitName = habit.name
                        )
                    }

                    observeProgress(habit)
                    observeCheckIns(habit.id)
                    observeRelapses(habit.id)
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
                        savingsText = formatSavings(habit.currency, progress)
                    )
                }
            }
        }
    }

    private fun observeCheckIns(habitId: String) {
        checkInsJob?.cancel()

        checkInsJob = viewModelScope.launch {
            getDailyCheckInsUseCase(habitId).collectLatest { checkIns ->
                val items = checkIns.take(3).map {
                    HomeContract.CheckInUi(
                        date = it.date.toString(),
                        mood = it.mood,
                        cravingLevel = it.cravingLevel
                    )
                }

                _state.update {
                    it.copy(recentCheckIns = items)
                }
            }
        }
    }

    private fun observeRelapses(habitId: String) {
        relapsesJob?.cancel()

        relapsesJob = viewModelScope.launch {
            getRelapsesUseCase(habitId).collectLatest { relapses ->
                val lastRelapse = relapses.firstOrNull()

                _state.update {
                    it.copy(
                        relapseCount = relapses.size,
                        lastRelapseDate = lastRelapse?.relapseDate?.let { date ->
                            formatDate(date)
                        } ?: ""
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

    private fun formatDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())
        return date.format(formatter)
    }

    private fun emitEffect(effect: HomeContract.UiEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }

    private fun observeRemoteConfig() {
        viewModelScope.launch {
            getRemoteConfigUseCase().collectLatest { config ->
                _state.update {
                    it.copy(
                        motivationalMessage = config.motivationalQuote,
                        emergencyTipsEnabled = config.emergencyTipsEnabled,
                        emergencyTipsMessage = config.remoteMessage
                    )
                }
            }
        }
    }
}
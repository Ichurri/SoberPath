package com.santiago.soberpath.presentation.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.santiago.soberpath.R
import com.santiago.soberpath.domain.model.Habit
import com.santiago.soberpath.domain.model.SobrietyProgress
import com.santiago.soberpath.domain.usecase.GetDailyCheckInsUseCase
import com.santiago.soberpath.domain.usecase.GetActiveHabitUseCase
import com.santiago.soberpath.domain.usecase.GetRemoteConfigUseCase
import com.santiago.soberpath.domain.usecase.GetSobrietyProgressUseCase
import com.santiago.soberpath.domain.usecase.RegisterRelapseUseCase
import com.santiago.soberpath.presentation.util.UiText
import java.time.LocalDate
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
    private val registerRelapseUseCase: RegisterRelapseUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(HomeContract.UiState())
    val state: StateFlow<HomeContract.UiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<HomeContract.UiEffect>()
    val effect = _effect.asSharedFlow()

    private var progressJob: Job? = null
    private var checkInsJob: Job? = null

    init {
        observeHabit()
        observeRemoteConfig()
    }

    fun onIntent(intent: HomeContract.UiIntent) {
        when (intent) {
            HomeContract.UiIntent.DailyCheckInClicked -> emitEffect(HomeContract.UiEffect.NavigateDailyCheckIn)
            HomeContract.UiIntent.MotivationClicked -> emitEffect(HomeContract.UiEffect.NavigateMotivation)
            HomeContract.UiIntent.MilestonesClicked -> emitEffect(HomeContract.UiEffect.NavigateMilestones)
            HomeContract.UiIntent.SettingsClicked -> emitEffect(HomeContract.UiEffect.NavigateSettings)
            HomeContract.UiIntent.RegisterRelapseClicked -> registerRelapse()
        }
    }

    private fun observeHabit() {
        viewModelScope.launch {
            getActiveHabitUseCase().collectLatest { habit ->
                if (habit == null) {
                    progressJob?.cancel()
                    checkInsJob?.cancel()
                    _state.update {
                        it.copy(
                            isLoading = false,
                            hasHabit = false,
                            habitName = "",
                            timeSinceRelapse = "",
                            savingsText = "",
                            motivationalMessage = "",
                            recentCheckIns = emptyList()
                        )
                    }
                } else {
                    _state.update { it.copy(isLoading = false, hasHabit = true, habitName = habit.name) }
                    observeProgress(habit)
                    observeCheckIns(habit.id)
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
                _state.update { it.copy(recentCheckIns = items) }
            }
        }
    }

    private fun registerRelapse() {
        if (!state.value.hasHabit) {
            emitEffect(
                HomeContract.UiEffect.ShowMessage(UiText.StringResource(R.string.message_no_active_habit))
            )
            return
        }
        viewModelScope.launch {
            val current = getActiveHabitUseCase().firstOrNull() ?: return@launch
            runCatching { registerRelapseUseCase(current.id, LocalDate.now()) }
                .onSuccess {
                    emitEffect(
                        HomeContract.UiEffect.ShowMessage(
                            UiText.StringResource(R.string.message_relapse_registered)
                        )
                    )
                }
                .onFailure {
                    emitEffect(
                        HomeContract.UiEffect.ShowMessage(UiText.StringResource(R.string.error_generic))
                    )
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

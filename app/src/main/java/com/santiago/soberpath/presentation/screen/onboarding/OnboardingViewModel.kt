package com.santiago.soberpath.presentation.screen.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.santiago.soberpath.R
import com.santiago.soberpath.domain.model.Habit
import com.santiago.soberpath.domain.usecase.CreateHabitUseCase
import com.santiago.soberpath.presentation.util.UiText
import java.time.LocalDate
import java.util.UUID
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val createHabitUseCase: CreateHabitUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(OnboardingContract.UiState())
    val state: StateFlow<OnboardingContract.UiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<OnboardingContract.UiEffect>()
    val effect = _effect.asSharedFlow()

    fun onIntent(intent: OnboardingContract.UiIntent) {
        when (intent) {
            is OnboardingContract.UiIntent.UpdateHabitName ->
                _state.update { it.copy(habitName = intent.value) }
            is OnboardingContract.UiIntent.UpdateCategory ->
                _state.update { it.copy(category = intent.value) }
            is OnboardingContract.UiIntent.UpdateStartDate ->
                _state.update { it.copy(startDate = intent.value) }
            is OnboardingContract.UiIntent.UpdateDailyCost ->
                _state.update { it.copy(dailyCost = intent.value) }
            is OnboardingContract.UiIntent.UpdateCurrency ->
                _state.update { it.copy(currency = intent.value) }
            OnboardingContract.UiIntent.Submit -> submit()
        }
    }

    private fun submit() {
        val current = state.value
        if (current.habitName.isBlank() ||
            current.category.isBlank() ||
            current.startDate.isBlank() ||
            current.dailyCost.isBlank() ||
            current.currency.isBlank()
        ) {
            emitMessage(R.string.error_required_fields)
            return
        }

        val parsedDate = runCatching { LocalDate.parse(current.startDate) }.getOrNull()
        if (parsedDate == null) {
            emitMessage(R.string.error_invalid_date)
            return
        }

        val dailyCost = current.dailyCost.toDoubleOrNull()
        if (dailyCost == null) {
            emitMessage(R.string.error_invalid_cost)
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val habit = Habit(
                id = UUID.randomUUID().toString(),
                name = current.habitName.trim(),
                category = current.category.trim(),
                startDate = parsedDate,
                lastRelapseDate = parsedDate,
                dailyCost = dailyCost,
                currency = current.currency.trim(),
                isActive = true
            )
            runCatching { createHabitUseCase(habit) }
                .onSuccess {
                    _effect.emit(OnboardingContract.UiEffect.NavigateHome)
                }
                .onFailure {
                    emitMessage(R.string.error_generic)
                }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun emitMessage(messageRes: Int) {
        viewModelScope.launch {
            _effect.emit(
                OnboardingContract.UiEffect.ShowMessage(UiText.StringResource(messageRes))
            )
        }
    }
}


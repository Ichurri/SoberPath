package com.santiago.soberpath.presentation.screen.recoverysetup

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

class RecoverySetupViewModel(
    private val createHabitUseCase: CreateHabitUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RecoverySetupContract.UiState())
    val state: StateFlow<RecoverySetupContract.UiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<RecoverySetupContract.UiEffect>()
    val effect = _effect.asSharedFlow()

    fun onIntent(intent: RecoverySetupContract.UiIntent) {
        when (intent) {
            is RecoverySetupContract.UiIntent.HabitNameChanged -> {
                _state.update {
                    it.copy(
                        habitName = intent.value,
                        nameError = null
                    )
                }
            }

            is RecoverySetupContract.UiIntent.DailyCostChanged -> {
                _state.update {
                    it.copy(
                        dailyCost = intent.value,
                        costError = null
                    )
                }
            }

            is RecoverySetupContract.UiIntent.CurrencyChanged -> {
                _state.update {
                    it.copy(currency = intent.value)
                }
            }

            RecoverySetupContract.UiIntent.SaveClicked -> {
                saveHabit()
            }
        }
    }

    private fun saveHabit() {
        val currentState = state.value

        val name = currentState.habitName.trim()
        val costText = currentState.dailyCost.trim().replace(",", ".")
        val parsedCost = if (costText.isBlank()) 0.0 else costText.toDoubleOrNull()

        var hasError = false

        if (name.isBlank()) {
            hasError = true
            _state.update {
                it.copy(
                    nameError = UiText.StringResource(
                        R.string.recovery_setup_error_name_required
                    )
                )
            }
        }

        if (parsedCost == null || parsedCost < 0.0) {
            hasError = true
            _state.update {
                it.copy(
                    costError = UiText.StringResource(
                        R.string.recovery_setup_error_invalid_cost
                    )
                )
            }
        }

        if (hasError) return

        val today = LocalDate.now()

        val habit = Habit(
            id = UUID.randomUUID().toString(),
            name = name,
            category = "recovery",
            startDate = today,
            lastRelapseDate = today,
            dailyCost = parsedCost ?: 0.0,
            currency = currentState.currency.trim().ifBlank { "Bs" },
            isActive = true
        )

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }

            runCatching {
                createHabitUseCase(habit)
            }.onSuccess {
                _state.update { it.copy(isSaving = false) }
                _effect.emit(RecoverySetupContract.UiEffect.NavigateHome)
            }.onFailure {
                _state.update { it.copy(isSaving = false) }
                _effect.emit(
                    RecoverySetupContract.UiEffect.ShowMessage(
                        UiText.StringResource(R.string.error_generic)
                    )
                )
            }
        }
    }
}
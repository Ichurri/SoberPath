package com.santiago.soberpath.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.santiago.soberpath.domain.usecase.GetActiveHabitUseCase
import com.santiago.soberpath.domain.usecase.IsOnboardingCompletedUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppStartViewModel(
    private val isOnboardingCompletedUseCase: IsOnboardingCompletedUseCase,
    private val getActiveHabitUseCase: GetActiveHabitUseCase
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = true,
        val nextDestination: String? = null
    )

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    init {
        checkStartDestination()
    }

    private fun checkStartDestination() {
        viewModelScope.launch {
            val completed = isOnboardingCompletedUseCase().first()

            val destination = if (!completed) {
                SoberDestination.Onboarding.route
            } else {
                val activeHabit = getActiveHabitUseCase().firstOrNull()

                if (activeHabit == null) {
                    SoberDestination.RecoverySetup.route
                } else {
                    SoberDestination.Home.route
                }
            }

            _state.update {
                it.copy(
                    isLoading = false,
                    nextDestination = destination
                )
            }
        }
    }
}
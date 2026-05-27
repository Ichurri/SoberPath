package com.santiago.soberpath.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.santiago.soberpath.domain.usecase.IsOnboardingCompletedUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppStartViewModel(
    private val isOnboardingCompletedUseCase: IsOnboardingCompletedUseCase
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = true,
        val startDestination: String? = null
    )

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val completed = isOnboardingCompletedUseCase().first()

            _state.update {
                it.copy(
                    isLoading = false,
                    startDestination = if (completed) {
                        SoberDestination.Home.route
                    } else {
                        SoberDestination.Onboarding.route
                    }
                )
            }
        }
    }
}
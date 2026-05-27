package com.santiago.soberpath.presentation.screen.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.santiago.soberpath.R
import com.santiago.soberpath.domain.model.OnboardingSlide
import com.santiago.soberpath.domain.usecase.CompleteOnboardingUseCase
import com.santiago.soberpath.domain.usecase.GetRemoteConfigUseCase
import com.santiago.soberpath.domain.usecase.RefreshRemoteConfigUseCase
import com.santiago.soberpath.presentation.util.UiText
import java.util.Locale
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val getRemoteConfigUseCase: GetRemoteConfigUseCase,
    private val refreshRemoteConfigUseCase: RefreshRemoteConfigUseCase,
    private val completeOnboardingUseCase: CompleteOnboardingUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingContract.UiState())
    val state: StateFlow<OnboardingContract.UiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<OnboardingContract.UiEffect>()
    val effect = _effect.asSharedFlow()

    init {
        loadOnboardingFromRemoteConfig()
    }

    fun onIntent(intent: OnboardingContract.UiIntent) {
        when (intent) {
            OnboardingContract.UiIntent.Previous -> previousSlide()
            OnboardingContract.UiIntent.Next -> nextSlide()
            OnboardingContract.UiIntent.Skip -> skipOnboarding()
            OnboardingContract.UiIntent.Start -> completeOnboarding()
        }
    }

    private fun loadOnboardingFromRemoteConfig() {
        viewModelScope.launch {
            runCatching {
                refreshRemoteConfigUseCase()
            }

            val config = getRemoteConfigUseCase().first()
            val language = getDeviceLanguage()

            val slides = config.onboardingConfig
                .sortedBy { it.id }
                .map { it.toUi(language) }

            _state.update {
                it.copy(
                    slides = slides,
                    currentIndex = 0,
                    isLoading = false
                )
            }
        }
    }

    private fun previousSlide() {
        _state.update {
            if (it.currentIndex > 0) {
                it.copy(currentIndex = it.currentIndex - 1)
            } else {
                it
            }
        }
    }

    private fun nextSlide() {
        _state.update {
            if (it.currentIndex < it.slides.lastIndex) {
                it.copy(currentIndex = it.currentIndex + 1)
            } else {
                it
            }
        }
    }

    private fun skipOnboarding() {
        viewModelScope.launch {
            _effect.emit(OnboardingContract.UiEffect.NavigateHome)
        }
    }

    private fun completeOnboarding() {
        viewModelScope.launch {
            runCatching {
                completeOnboardingUseCase()
            }.onSuccess {
                _effect.emit(OnboardingContract.UiEffect.NavigateHome)
            }.onFailure {
                _effect.emit(
                    OnboardingContract.UiEffect.ShowMessage(
                        UiText.StringResource(R.string.error_generic)
                    )
                )
            }
        }
    }

    private fun getDeviceLanguage(): String {
        return when (Locale.getDefault().language.lowercase(Locale.ROOT)) {
            "es" -> "es"
            "fr" -> "fr"
            else -> "en"
        }
    }

    private fun OnboardingSlide.toUi(language: String): OnboardingContract.OnboardingSlideUi {
        return OnboardingContract.OnboardingSlideUi(
            id = id,
            title = title.localized(language),
            description = description.localized(language),
            imageUrl = imageUrl.localized(language)
        )
    }

    private fun Map<String, String>.localized(language: String): String {
        return this[language]
            ?: this["en"]
            ?: this["es"]
            ?: values.firstOrNull()
            ?: ""
    }
}
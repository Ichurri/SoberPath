package com.santiago.soberpath.presentation.screen.onboarding

import com.santiago.soberpath.presentation.util.UiText

object OnboardingContract {

    data class UiState(
        val slides: List<OnboardingSlideUi> = emptyList(),
        val currentIndex: Int = 0,
        val isLoading: Boolean = true
    ) {
        val currentSlide: OnboardingSlideUi?
            get() = slides.getOrNull(currentIndex)

        val isFirstSlide: Boolean
            get() = currentIndex == 0

        val isLastSlide: Boolean
            get() = slides.isNotEmpty() && currentIndex == slides.lastIndex
    }

    data class OnboardingSlideUi(
        val id: Int,
        val title: String,
        val description: String,
        val imageUrl: String
    )

    sealed interface UiIntent {
        data object Previous : UiIntent
        data object Next : UiIntent
        data object Skip : UiIntent
        data object Start : UiIntent
    }

    sealed interface UiEffect {
        data object NavigateHome : UiEffect
        data class ShowMessage(val message: UiText) : UiEffect
    }
}
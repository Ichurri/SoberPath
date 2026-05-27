package com.santiago.soberpath.domain.usecase

import com.santiago.soberpath.domain.repository.OnboardingRepository

class CompleteOnboardingUseCase(
    private val repository: OnboardingRepository
) {
    suspend operator fun invoke() {
        repository.setOnboardingCompleted(true)
    }
}
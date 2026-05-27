package com.santiago.soberpath.domain.usecase

import com.santiago.soberpath.domain.repository.OnboardingRepository
import kotlinx.coroutines.flow.Flow

class IsOnboardingCompletedUseCase(
    private val repository: OnboardingRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return repository.isOnboardingCompleted()
    }
}
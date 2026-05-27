package com.santiago.soberpath.data.repository

import com.santiago.soberpath.data.local.preferences.OnboardingPreferencesDataSource
import com.santiago.soberpath.domain.repository.OnboardingRepository
import kotlinx.coroutines.flow.Flow

class OnboardingRepositoryImpl(
    private val preferencesDataSource: OnboardingPreferencesDataSource
) : OnboardingRepository {

    override fun isOnboardingCompleted(): Flow<Boolean> {
        return preferencesDataSource.isOnboardingCompleted()
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        preferencesDataSource.setOnboardingCompleted(completed)
    }
}
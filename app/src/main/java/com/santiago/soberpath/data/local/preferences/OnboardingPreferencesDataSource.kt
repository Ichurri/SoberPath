package com.santiago.soberpath.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.soberPathDataStore by preferencesDataStore(
    name = "soberpath_preferences"
)

class OnboardingPreferencesDataSource(
    private val context: Context
) {

    fun isOnboardingCompleted(): Flow<Boolean> {
        return context.soberPathDataStore.data.map { preferences ->
            preferences[ONBOARDING_COMPLETED] ?: false
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.soberPathDataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED] = completed
        }
    }

    companion object {
        private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }
}
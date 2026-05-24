package com.santiago.soberpath.presentation.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.santiago.soberpath.BuildConfig
import com.santiago.soberpath.R
import com.santiago.soberpath.domain.usecase.RefreshRemoteConfigUseCase
import com.santiago.soberpath.presentation.util.UiText
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val refreshRemoteConfigUseCase: RefreshRemoteConfigUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(
        SettingsContract.UiState(appVersion = BuildConfig.VERSION_NAME)
    )
    val state: StateFlow<SettingsContract.UiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SettingsContract.UiEffect>()
    val effect = _effect.asSharedFlow()

    fun onIntent(intent: SettingsContract.UiIntent) {
        when (intent) {
            is SettingsContract.UiIntent.UpdateReminderEnabled ->
                _state.update { it.copy(reminderEnabled = intent.value) }
            is SettingsContract.UiIntent.UpdateReminderTime ->
                _state.update { it.copy(reminderTime = intent.value) }
            SettingsContract.UiIntent.RefreshRemoteConfig -> refreshRemoteConfig()
            SettingsContract.UiIntent.Back -> emitEffect(SettingsContract.UiEffect.NavigateBack)
        }
    }

    private fun refreshRemoteConfig() {
        if (!BuildConfig.DEBUG) return
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }
            val success = runCatching { refreshRemoteConfigUseCase() }.getOrDefault(false)
            val message = if (success) {
                UiText.StringResource(R.string.message_remote_config_updated)
            } else {
                UiText.StringResource(R.string.message_remote_config_failed)
            }
            emitMessage(message)
            _state.update { it.copy(isRefreshing = false) }
        }
    }

    private fun emitEffect(effect: SettingsContract.UiEffect) {
        viewModelScope.launch { _effect.emit(effect) }
    }

    private fun emitMessage(message: UiText) {
        viewModelScope.launch { _effect.emit(SettingsContract.UiEffect.ShowMessage(message)) }
    }
}


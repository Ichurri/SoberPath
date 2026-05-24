package com.santiago.soberpath.presentation.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.santiago.soberpath.BuildConfig
import com.santiago.soberpath.R
import com.santiago.soberpath.domain.usecase.RefreshRemoteConfigUseCase
import com.santiago.soberpath.notification.NotificationScheduler
import com.santiago.soberpath.presentation.util.UiText
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val refreshRemoteConfigUseCase: RefreshRemoteConfigUseCase,
    private val notificationScheduler: NotificationScheduler
) : ViewModel() {
    private val _state = MutableStateFlow(
        SettingsContract.UiState(appVersion = BuildConfig.VERSION_NAME)
    )
    val state: StateFlow<SettingsContract.UiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SettingsContract.UiEffect>()
    val effect = _effect.asSharedFlow()

    private var pendingReminderHour: Int? = null

    fun onIntent(intent: SettingsContract.UiIntent) {
        when (intent) {
            is SettingsContract.UiIntent.UpdateReminderTime ->
                _state.update { it.copy(reminderTime = intent.value) }
            is SettingsContract.UiIntent.ToggleReminder -> handleReminderToggle(intent.value)
            is SettingsContract.UiIntent.NotificationPermissionResult -> handlePermissionResult(intent.granted)
            SettingsContract.UiIntent.RefreshRemoteConfig -> refreshRemoteConfig()
            SettingsContract.UiIntent.Back -> emitEffect(SettingsContract.UiEffect.NavigateBack)
        }
    }

    private fun handleReminderToggle(enabled: Boolean) {
        if (!enabled) {
            notificationScheduler.cancelDailyReminder()
            _state.update { it.copy(reminderEnabled = false) }
            return
        }

        val hour = parseReminderHour(_state.value.reminderTime)
        if (hour == null) {
            emitMessage(UiText.StringResource(R.string.message_invalid_time))
            return
        }

        if (!notificationScheduler.canPostNotifications()) {
            pendingReminderHour = hour
            emitEffect(SettingsContract.UiEffect.RequestNotificationPermission)
            return
        }

        when (notificationScheduler.scheduleDailyReminder(hour)) {
            NotificationScheduler.ScheduleResult.Scheduled ->
                _state.update { it.copy(reminderEnabled = true) }
            NotificationScheduler.ScheduleResult.MissingPermission ->
                emitMessage(UiText.StringResource(R.string.message_notification_permission_required))
            NotificationScheduler.ScheduleResult.InvalidTime ->
                emitMessage(UiText.StringResource(R.string.message_invalid_time))
            is NotificationScheduler.ScheduleResult.Failed ->
                emitMessage(UiText.StringResource(R.string.error_generic))
        }
    }

    private fun handlePermissionResult(granted: Boolean) {
        val hour = pendingReminderHour
        pendingReminderHour = null
        if (!granted) {
            emitMessage(UiText.StringResource(R.string.message_notification_permission_denied))
            return
        }
        if (hour == null) return
        when (notificationScheduler.scheduleDailyReminder(hour)) {
            NotificationScheduler.ScheduleResult.Scheduled ->
                _state.update { it.copy(reminderEnabled = true) }
            else -> emitMessage(UiText.StringResource(R.string.error_generic))
        }
    }

    private fun parseReminderHour(value: String): Int? {
        val parts = value.split(":")
        if (parts.isEmpty()) return null
        val hour = parts[0].toIntOrNull() ?: return null
        return if (hour in 0..23) hour else null
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


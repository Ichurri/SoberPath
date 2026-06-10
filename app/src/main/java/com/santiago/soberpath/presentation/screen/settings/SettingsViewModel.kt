package com.santiago.soberpath.presentation.screen.settings

import android.content.Context
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
    private val context: Context,
    private val refreshRemoteConfigUseCase: RefreshRemoteConfigUseCase,
    private val notificationScheduler: NotificationScheduler
) : ViewModel() {
    private val _state = MutableStateFlow(
        SettingsContract.UiState(appVersion = BuildConfig.VERSION_NAME)
    )
    val state: StateFlow<SettingsContract.UiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SettingsContract.UiEffect>()
    val effect = _effect.asSharedFlow()

    private var pendingReminderTime: String? = null

    private val PREFS_NAME = "soberpath_prefs"
    private val KEY_REMINDER_ENABLED = "reminder_enabled"
    private val KEY_REMINDER_TIME = "reminder_time"

    init {
        loadPersistedSettings()
    }

    private fun loadPersistedSettings() {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val enabled = prefs.getBoolean(KEY_REMINDER_ENABLED, false)
        val time = prefs.getString(KEY_REMINDER_TIME, "09:00") ?: "09:00"
        _state.update {
            it.copy(
                reminderEnabled = enabled,
                reminderTime = time
            )
        }
    }

    fun onIntent(intent: SettingsContract.UiIntent) {
        when (intent) {
            is SettingsContract.UiIntent.UpdateReminderTime -> {
                val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                prefs.edit().putString(KEY_REMINDER_TIME, intent.value).apply()
                _state.update { it.copy(reminderTime = intent.value) }
                if (_state.value.reminderEnabled) {
                    val timePair = parseReminderTime(intent.value)
                    if (timePair != null) {
                        notificationScheduler.scheduleDailyReminder(timePair.first, timePair.second)
                    }
                }
            }
            is SettingsContract.UiIntent.ToggleReminder -> handleReminderToggle(intent.value)
            is SettingsContract.UiIntent.NotificationPermissionResult -> handlePermissionResult(intent.granted)
            SettingsContract.UiIntent.RefreshRemoteConfig -> refreshRemoteConfig()
            SettingsContract.UiIntent.Back -> emitEffect(SettingsContract.UiEffect.NavigateBack)
        }
    }

    private fun handleReminderToggle(enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (!enabled) {
            notificationScheduler.cancelDailyReminder()
            prefs.edit().putBoolean(KEY_REMINDER_ENABLED, false).apply()
            _state.update { it.copy(reminderEnabled = false) }
            return
        }

        val timePair = parseReminderTime(_state.value.reminderTime)
        if (timePair == null) {
            emitMessage(UiText.StringResource(R.string.message_invalid_time))
            return
        }

        if (!notificationScheduler.canPostNotifications()) {
            pendingReminderTime = _state.value.reminderTime
            emitEffect(SettingsContract.UiEffect.RequestNotificationPermission)
            return
        }

        when (notificationScheduler.scheduleDailyReminder(timePair.first, timePair.second)) {
            NotificationScheduler.ScheduleResult.Scheduled -> {
                prefs.edit().putBoolean(KEY_REMINDER_ENABLED, true).apply()
                _state.update { it.copy(reminderEnabled = true) }
            }
            NotificationScheduler.ScheduleResult.MissingPermission ->
                emitMessage(UiText.StringResource(R.string.message_notification_permission_required))
            NotificationScheduler.ScheduleResult.InvalidTime ->
                emitMessage(UiText.StringResource(R.string.message_invalid_time))
            is NotificationScheduler.ScheduleResult.Failed ->
                emitMessage(UiText.StringResource(R.string.error_generic))
        }
    }

    private fun handlePermissionResult(granted: Boolean) {
        val time = pendingReminderTime
        pendingReminderTime = null
        if (!granted) {
            emitMessage(UiText.StringResource(R.string.message_notification_permission_denied))
            return
        }
        if (time == null) return
        val timePair = parseReminderTime(time) ?: return
        when (notificationScheduler.scheduleDailyReminder(timePair.first, timePair.second)) {
            NotificationScheduler.ScheduleResult.Scheduled -> {
                val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                prefs.edit().putBoolean(KEY_REMINDER_ENABLED, true).apply()
                _state.update { it.copy(reminderEnabled = true) }
            }
            else -> emitMessage(UiText.StringResource(R.string.error_generic))
        }
    }

    private fun parseReminderTime(value: String): Pair<Int, Int>? {
        val parts = value.split(":")
        if (parts.size < 2) return null
        val hour = parts[0].toIntOrNull() ?: return null
        val minute = parts[1].toIntOrNull() ?: return null
        return if (hour in 0..23 && minute in 0..59) Pair(hour, minute) else null
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


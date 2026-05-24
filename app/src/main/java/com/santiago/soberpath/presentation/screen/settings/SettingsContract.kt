package com.santiago.soberpath.presentation.screen.settings

import com.santiago.soberpath.presentation.util.UiText

object SettingsContract {
    data class UiState(
        val reminderEnabled: Boolean = false,
        val reminderTime: String = "09:00",
        val appVersion: String = "",
        val isRefreshing: Boolean = false
    )

    sealed interface UiIntent {
        data class UpdateReminderTime(val value: String) : UiIntent
        data class ToggleReminder(val value: Boolean) : UiIntent
        data class NotificationPermissionResult(val granted: Boolean) : UiIntent
        object RefreshRemoteConfig : UiIntent
        object Back : UiIntent
    }

    sealed interface UiEffect {
        object NavigateBack : UiEffect
        object RequestNotificationPermission : UiEffect
        data class ShowMessage(val message: UiText) : UiEffect
    }
}


package com.santiago.soberpath.data.remote.config

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.santiago.soberpath.domain.model.AppConfig
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

class FirebaseRemoteConfigDataSource(
    private val remoteConfig: FirebaseRemoteConfig
) {
    private val defaults = mapOf(
        KEY_REMOTE_MESSAGE to "",
        KEY_EMERGENCY_TIPS_ENABLED to false,
        KEY_DAILY_REMINDER_ENABLED_DEFAULT to false,
        KEY_MIN_SUPPORTED_VERSION to "1",
        KEY_MOTIVATIONAL_QUOTE to "",
        KEY_SHOW_MILESTONE_ANIMATION to false,
        KEY_CHECKIN_REQUIRED to false
    )

    fun applyDefaults() {
        remoteConfig.setDefaultsAsync(defaults)
    }

    suspend fun fetchAndActivate(): Boolean = suspendCancellableCoroutine { cont ->
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (cont.isCancelled) return@addOnCompleteListener
            cont.resume(task.isSuccessful && task.result == true)
        }
    }

    suspend fun refresh(): Boolean {
        return fetchAndActivate()
    }

    fun getConfig(): AppConfig {
        val minVersion = remoteConfig.getString(KEY_MIN_SUPPORTED_VERSION).toIntOrNull() ?: 1
        return AppConfig(
            dailyReminderEnabled = remoteConfig.getBoolean(KEY_DAILY_REMINDER_ENABLED_DEFAULT),
            dailyReminderHour = DEFAULT_DAILY_REMINDER_HOUR,
            remoteMessage = remoteConfig.getString(KEY_REMOTE_MESSAGE),
            emergencyTipsEnabled = remoteConfig.getBoolean(KEY_EMERGENCY_TIPS_ENABLED),
            minSupportedVersion = minVersion,
            motivationalQuote = remoteConfig.getString(KEY_MOTIVATIONAL_QUOTE),
            showMilestoneAnimation = remoteConfig.getBoolean(KEY_SHOW_MILESTONE_ANIMATION),
            checkinRequired = remoteConfig.getBoolean(KEY_CHECKIN_REQUIRED)
        )
    }

    companion object {
        const val KEY_REMOTE_MESSAGE = "remote_message"
        const val KEY_EMERGENCY_TIPS_ENABLED = "emergency_tips_enabled"
        const val KEY_DAILY_REMINDER_ENABLED_DEFAULT = "daily_reminder_enabled_default"
        const val KEY_MIN_SUPPORTED_VERSION = "min_supported_version"
        const val KEY_MOTIVATIONAL_QUOTE = "motivational_quote"
        const val KEY_SHOW_MILESTONE_ANIMATION = "show_milestone_animation"
        const val KEY_CHECKIN_REQUIRED = "checkin_required"

        private const val DEFAULT_DAILY_REMINDER_HOUR = 9
    }
}


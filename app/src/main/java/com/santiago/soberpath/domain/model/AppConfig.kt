package com.santiago.soberpath.domain.model

// App configuration from local defaults and Remote Config.
data class AppConfig(
    val dailyReminderEnabled: Boolean,
    val dailyReminderHour: Int,
    val remoteMessage: String,
    val emergencyTipsEnabled: Boolean,
    val minSupportedVersion: Int,
    val motivationalQuote: String,
    val showMilestoneAnimation: Boolean,
    val checkinRequired: Boolean,
    val onboardingConfig: List<OnboardingSlide> = emptyList()
)

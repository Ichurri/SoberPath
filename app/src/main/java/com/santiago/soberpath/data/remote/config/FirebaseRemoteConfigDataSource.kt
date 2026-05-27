package com.santiago.soberpath.data.remote.config

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.santiago.soberpath.domain.model.AppConfig
import com.santiago.soberpath.domain.model.OnboardingSlide
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONArray
import org.json.JSONObject

class FirebaseRemoteConfigDataSource(
    private val remoteConfig: FirebaseRemoteConfig
) {

    init {
        applyDefaults()
    }

    private fun applyDefaults() {
        remoteConfig.setDefaultsAsync(
            mapOf(
                KEY_REMOTE_MESSAGE to "",
                KEY_EMERGENCY_TIPS_ENABLED to false,
                KEY_DAILY_REMINDER_ENABLED_DEFAULT to false,
                KEY_MIN_SUPPORTED_VERSION to "1",
                KEY_MOTIVATIONAL_QUOTE to "",
                KEY_SHOW_MILESTONE_ANIMATION to false,
                KEY_CHECKIN_REQUIRED to false,
                KEY_ONBOARDING_CONFIG to DEFAULT_ONBOARDING_CONFIG
            )
        )
    }

    suspend fun refresh(): Boolean = suspendCancellableCoroutine { continuation ->
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (continuation.isCancelled) return@addOnCompleteListener
            continuation.resume(task.isSuccessful)
        }
    }

    fun getConfig(): AppConfig {
        val onboardingJson = remoteConfig.getString(KEY_ONBOARDING_CONFIG)
            .ifBlank { DEFAULT_ONBOARDING_CONFIG }

        return AppConfig(
            dailyReminderEnabled = remoteConfig.getBoolean(KEY_DAILY_REMINDER_ENABLED_DEFAULT),
            dailyReminderHour = DEFAULT_DAILY_REMINDER_HOUR,
            remoteMessage = remoteConfig.getString(KEY_REMOTE_MESSAGE),
            emergencyTipsEnabled = remoteConfig.getBoolean(KEY_EMERGENCY_TIPS_ENABLED),
            minSupportedVersion = remoteConfig.getString(KEY_MIN_SUPPORTED_VERSION).toIntOrNull() ?: 1,
            motivationalQuote = remoteConfig.getString(KEY_MOTIVATIONAL_QUOTE),
            showMilestoneAnimation = remoteConfig.getBoolean(KEY_SHOW_MILESTONE_ANIMATION),
            checkinRequired = remoteConfig.getBoolean(KEY_CHECKIN_REQUIRED),
            onboardingConfig = parseOnboardingConfig(onboardingJson)
        )
    }

    private fun parseOnboardingConfig(json: String): List<OnboardingSlide> {
        return runCatching {
            val array = if (json.trim().startsWith("{")) {
                JSONObject(json).getJSONArray(KEY_ONBOARDING_CONFIG)
            } else {
                JSONArray(json)
            }

            List(array.length()) { index ->
                val item = array.getJSONObject(index)

                OnboardingSlide(
                    id = item.optInt("id", index + 1),
                    title = item.getJSONObject("title").toStringMap(),
                    description = item.getJSONObject("description").toStringMap(),
                    imageUrl = item.getJSONObject("image_url").toStringMap()
                )
            }
        }.getOrDefault(emptyList())
    }

    private fun JSONObject.toStringMap(): Map<String, String> {
        val map = mutableMapOf<String, String>()
        val keys = keys()

        while (keys.hasNext()) {
            val key = keys.next()
            map[key] = optString(key)
        }

        return map
    }

    companion object {
        const val KEY_REMOTE_MESSAGE = "remote_message"
        const val KEY_EMERGENCY_TIPS_ENABLED = "emergency_tips_enabled"
        const val KEY_DAILY_REMINDER_ENABLED_DEFAULT = "daily_reminder_enabled_default"
        const val KEY_MIN_SUPPORTED_VERSION = "min_supported_version"
        const val KEY_MOTIVATIONAL_QUOTE = "motivational_quote"
        const val KEY_SHOW_MILESTONE_ANIMATION = "show_milestone_animation"
        const val KEY_CHECKIN_REQUIRED = "checkin_required"
        const val KEY_ONBOARDING_CONFIG = "onboarding_config"

        private const val DEFAULT_DAILY_REMINDER_HOUR = 9

        private const val DEFAULT_ONBOARDING_CONFIG = """
[
  {
    "id": 1,
    "title": {
      "es": "Comienza tu camino",
      "en": "Start your journey",
      "fr": "Commencez votre chemin"
    },
    "description": {
      "es": "SoberPath te acompaña en tu proceso de cambio, ayudándote a mantener el enfoque día a día.",
      "en": "SoberPath supports your recovery journey and helps you stay focused day by day.",
      "fr": "SoberPath vous accompagne dans votre processus de changement et vous aide à rester concentré chaque jour."
    },
    "image_url": {
      "es": "https://placehold.co/800x600/png?text=SoberPath+Inicio",
      "en": "https://placehold.co/800x600/png?text=SoberPath+Start",
      "fr": "https://placehold.co/800x600/png?text=SoberPath+Debut"
    }
  },
  {
    "id": 2,
    "title": {
      "es": "Registra tu progreso",
      "en": "Track your progress",
      "fr": "Suivez votre progression"
    },
    "description": {
      "es": "Visualiza tus días de avance, el tiempo logrado y el dinero ahorrado durante tu proceso.",
      "en": "View your sober days, achieved time, and money saved during your process.",
      "fr": "Visualisez vos jours de progrès, le temps atteint et l'argent économisé pendant votre parcours."
    },
    "image_url": {
      "es": "https://placehold.co/800x600/png?text=Progreso",
      "en": "https://placehold.co/800x600/png?text=Progress",
      "fr": "https://placehold.co/800x600/png?text=Progression"
    }
  },
  {
    "id": 3,
    "title": {
      "es": "Haz check-in diario",
      "en": "Daily check-in",
      "fr": "Bilan quotidien"
    },
    "description": {
      "es": "Registra tu estado de ánimo, tus impulsos y tu compromiso para fortalecer tu recuperación.",
      "en": "Record your mood, cravings, and commitment to strengthen your recovery.",
      "fr": "Enregistrez votre humeur, vos envies et votre engagement pour renforcer votre rétablissement."
    },
    "image_url": {
      "es": "https://placehold.co/800x600/png?text=Check-in+Diario",
      "en": "https://placehold.co/800x600/png?text=Daily+Check-in",
      "fr": "https://placehold.co/800x600/png?text=Bilan+Quotidien"
    }
  },
  {
    "id": 4,
    "title": {
      "es": "Mantén tu motivación",
      "en": "Stay motivated",
      "fr": "Restez motive"
    },
    "description": {
      "es": "Guarda tus razones personales, revisa tus logros y continúa avanzando un día a la vez.",
      "en": "Save your personal reasons, review your achievements, and keep moving forward one day at a time.",
      "fr": "Gardez vos raisons personnelles, consultez vos réussites et avancez un jour à la fois."
    },
    "image_url": {
      "es": "https://placehold.co/800x600/png?text=Motivacion",
      "en": "https://placehold.co/800x600/png?text=Motivation",
      "fr": "https://placehold.co/800x600/png?text=Motivation"
    }
  }
]
"""
    }
}
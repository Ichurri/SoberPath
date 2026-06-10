package com.santiago.soberpath.data.remote.config

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.santiago.soberpath.domain.model.AppConfig
import com.santiago.soberpath.domain.model.OnboardingSlide
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose
import org.json.JSONArray
import org.json.JSONObject

class FirebaseRemoteConfigDataSource(
    private val remoteConfig: FirebaseRemoteConfig
) {

    fun observeConfigUpdates(): Flow<AppConfig> = callbackFlow {
        applyDefaults()
        trySend(getConfig())

        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                trySend(getConfig())
            }
        }

        val registration = remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                remoteConfig.activate().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        trySend(getConfig())
                    }
                }
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                // Ignore or log error
            }
        })

        awaitClose {
            registration.remove()
        }
    }

    init {
        applyDefaults()
    }

    fun applyDefaults() {
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
            val root = JSONObject(json)
            val array = root.optJSONArray(KEY_ONBOARDING_CONFIG) 
                ?: if (json.trim().startsWith("[")) JSONArray(json) else JSONArray()

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
{
  "onboarding_config": [
    {
      "id": 1,
      "title": {
        "es": "Recupera el Control",
        "en": "Take Back Control"
      },
      "description": {
        "es": "Registra el tiempo exacto que llevas libre de tus hábitos con un cronómetro interactivo de días, horas, minutos y segundos. ¡Cada segundo cuenta!",
        "en": "Track the exact time you have been free from habits with an interactive clock showing days, hours, minutes, and seconds. Every second counts!"
      },
      "image_url": {
        "es": "https://images.unsplash.com/photo-1506744038136-46273834b3fb?q=80&w=600&auto=format&fit=crop",
        "en": "https://images.unsplash.com/photo-1506744038136-46273834b3fb?q=80&w=600&auto=format&fit=crop"
      }
    },
    {
      "id": 2,
      "title": {
        "es": "Celebra tus Logros",
        "en": "Celebrate Your Milestones"
      },
      "description": {
        "es": "Establece metas de abstinencia a corto y largo plazo. Gana medallas virtuales y celebra cada logro superado en tu camino de recuperación.",
        "en": "Set short and long-term abstinence goals. Earn virtual badges and celebrate every milestone achieved on your journey."
      },
      "image_url": {
        "es": "https://images.unsplash.com/photo-1501555088652-021faa106b9b?q=80&w=600&auto=format&fit=crop",
        "en": "https://images.unsplash.com/photo-1501555088652-021faa106b9b?q=80&w=600&auto=format&fit=crop"
      }
    },
    {
      "id": 3,
      "title": {
        "es": "Tu Diario Personal",
        "en": "Your Daily Journal"
      },
      "description": {
        "es": "Realiza autoevaluaciones diarias de tu estado de ánimo, niveles de ansiedad y tentación para mantener un seguimiento honesto de tu progreso.",
        "en": "Perform daily self-assessments of your mood, anxiety levels, and temptation to maintain an honest track of your progress."
      },
      "image_url": {
        "es": "https://images.unsplash.com/photo-1484480974693-6ca0a78fb36b?q=80&w=600&auto=format&fit=crop",
        "en": "https://images.unsplash.com/photo-1484480974693-6ca0a78fb36b?q=80&w=600&auto=format&fit=crop"
      }
    },
    {
      "id": 4,
      "title": {
        "es": "Un Día a la Vez",
        "en": "One Day at a Time"
      },
      "description": {
        "es": "Registra tus deslices con honestidad para reiniciar tu contador, accede a consejos motivacionales y mantén el enfoque en tu bienestar diario.",
        "en": "Register relapses honestly to reset your counter, access motivational advice, and stay focused on your daily well-being."
      },
      "image_url": {
        "es": "https://images.unsplash.com/photo-1490730141103-6cac27aaab94?q=80&w=600&auto=format&fit=crop",
        "en": "https://images.unsplash.com/photo-1490730141103-6cac27aaab94?q=80&w=600&auto=format&fit=crop"
      }
    }
  ]
}
"""
    }
}

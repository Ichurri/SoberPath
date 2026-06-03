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
        "es": "¡Organiza tu día!",
        "en": "Organize your day!",
        "fr": "Organisez votre journée !"
      },
      "description": {
        "es": "Gestiona tareas, proyectos y prioridades de forma sencilla con FlowWise.",
        "en": "Easily manage tasks, projects, and priorities with FlowWise.",
        "fr": "Gérez facilement vos tâches, projets et priorités avec FlowWise."
      },
      "image_url": {
        "es": "https://placehold.co/800x600/png?text=Slide+1+ES",
        "en": "https://placehold.co/800x600/png?text=Slide+1+EN",
        "fr": "https://placehold.co/800x600/png?text=Slide+1+FR"
      }
    },
    {
      "id": 2,
      "title": {
        "es": "Trabaja en equipo",
        "en": "Teamwork",
        "fr": "Travail d'équipe"
      },
      "description": {
        "es": "Colabora en tiempo real con tus compañeros y mantén a todos sincronizados.",
        "en": "Collaborate in real-time with your teammates and keep everyone in sync.",
        "fr": "Collaborez en temps réel con vos collègues et gardez tout le monde synchronisé."
      },
      "image_url": {
        "es": "https://placehold.co/800x600/png?text=Slide+2+ES",
        "en": "https://placehold.co/800x600/png?text=Slide+2+EN",
        "fr": "https://placehold.co/800x600/png?text=Slide+2+FR"
      }
    },
    {
      "id": 3,
      "title": {
        "es": "Mide tu progreso",
        "en": "Track your progress",
        "fr": "Mesurez votre progression"
      },
      "description": {
        "es": "Accede a estadísticas detalladas sobre tu productividad y alcanza tus metas.",
        "en": "Access detailed statistics about your productivity and reach your goals.",
        "fr": "Accédez à des statistiques détaillées sur votre productivité et atteignez vos objectifs."
      },
      "image_url": {
        "es": "https://placehold.co/800x600/png?text=Slide+3+ES",
        "en": "https://placehold.co/800x600/png?text=Slide+3+EN",
        "fr": "https://placehold.co/800x600/png?text=Slide+3+FR"
      }
    },
    {
      "id": 4,
      "title": {
        "es": "Todo listo para empezar",
        "en": "All ready to start",
        "fr": "Tout est prêt"
      },
      "description": {
        "es": "Crea tu cuenta ahora y transforma tu manera de trabajar desde hoy mismo.",
        "en": "Create your account now and transform the way you work today.",
        "fr": "Créez votre compte maintenant et transformez votre façon de travailler dès aujourd'hui."
      },
      "image_url": {
        "es": "https://placehold.co/800x600/png?text=Slide+4+ES",
        "en": "https://placehold.co/800x600/png?text=Slide+4+EN",
        "fr": "https://placehold.co/800x600/png?text=Slide+4+FR"
      }
    }
  ]
}
"""
    }
}

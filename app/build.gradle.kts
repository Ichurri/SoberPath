import java.net.URL
import java.net.HttpURLConnection
import java.net.URI

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.ksp)
    alias(libs.plugins.firebase.app.distribution) apply false
}

android {
    namespace = "com.santiago.soberpath"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.santiago.soberpath"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

if (providers.gradleProperty("enableAppDistribution").orNull == "true") {
    apply(plugin = "com.google.firebase.appdistribution")
    extensions.configure<com.google.firebase.appdistribution.gradle.AppDistributionExtension> {
        artifactType = "APK"
        releaseNotesFile = "app_distribution/release_notes.txt"
        groups = "internal-testers"
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.text)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.runtime.saveable)
    implementation(libs.coil.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.config)
    implementation(libs.firebase.messaging)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.datastore.preferences)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}

tasks.register("pullTranslations") {
    group = "localization"
    description = "Sincroniza y descarga las traducciones desde localise.biz API usando -PlocoKey"

    doLast {
        val key = project.findProperty("locoKey") as? String
        if (key.isNullOrBlank()) {
            throw GradleException("Error: Por favor proporciona tu API Key de localise.biz usando -PlocoKey=TU_KEY")
        }

        val locales = mapOf(
            "es" to file("src/main/res/values/strings.xml"),
            "en" to file("src/main/res/values-en/strings.xml")
        )

        locales.forEach { (locale, targetFile) ->
            println("Descargando traducción para el idioma: $locale...")
            val url = URI("https://localise.biz/api/export/locale/$locale.xml?key=$key").toURL()
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            if (connection.responseCode == 200) {
                targetFile.parentFile.mkdirs()
                targetFile.writeBytes(connection.inputStream.readAllBytes())
                println("✔ Guardado correctamente: $locale -> ${targetFile.absolutePath}")
            } else {
                val errorText = connection.errorStream?.bufferedReader()?.readText() ?: "Error desconocido"
                println("✘ Error al descargar $locale: HTTP ${connection.responseCode} - $errorText")
            }
        }
    }
}
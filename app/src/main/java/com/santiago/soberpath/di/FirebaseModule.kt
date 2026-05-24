package com.santiago.soberpath.di

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.santiago.soberpath.BuildConfig
import com.santiago.soberpath.data.remote.config.FirebaseRemoteConfigDataSource
import org.koin.dsl.module

object FirebaseModule {
    val module = module {
        single {
            val remoteConfig = FirebaseRemoteConfig.getInstance()
            val settings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(
                    if (BuildConfig.DEBUG) 0 else 3600
                )
                .build()
            remoteConfig.setConfigSettingsAsync(settings)
            remoteConfig
        }
        single { FirebaseRemoteConfigDataSource(get()) }
    }
}

package com.santiago.soberpath.data.repository

import com.santiago.soberpath.data.remote.config.FirebaseRemoteConfigDataSource
import com.santiago.soberpath.domain.model.AppConfig
import com.santiago.soberpath.domain.repository.ConfigRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ConfigRepositoryImpl(
    private val remoteConfigDataSource: FirebaseRemoteConfigDataSource
) : ConfigRepository {
    override fun getRemoteConfig(): Flow<AppConfig> = flow {
        remoteConfigDataSource.applyDefaults()
        emit(remoteConfigDataSource.getConfig())

        val updated = runCatching { remoteConfigDataSource.fetchAndActivate() }
            .getOrDefault(false)
        if (updated) {
            emit(remoteConfigDataSource.getConfig())
        }
    }
}


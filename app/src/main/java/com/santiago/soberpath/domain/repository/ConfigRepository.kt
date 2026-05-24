package com.santiago.soberpath.domain.repository

import com.santiago.soberpath.domain.model.AppConfig
import kotlinx.coroutines.flow.Flow

interface ConfigRepository {
    fun getRemoteConfig(): Flow<AppConfig>
}


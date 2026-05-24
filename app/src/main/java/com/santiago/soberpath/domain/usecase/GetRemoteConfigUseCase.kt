package com.santiago.soberpath.domain.usecase

import com.santiago.soberpath.domain.model.AppConfig
import com.santiago.soberpath.domain.repository.ConfigRepository
import kotlinx.coroutines.flow.Flow

class GetRemoteConfigUseCase(
    private val configRepository: ConfigRepository
) {
    operator fun invoke(): Flow<AppConfig> = configRepository.getRemoteConfig()
}


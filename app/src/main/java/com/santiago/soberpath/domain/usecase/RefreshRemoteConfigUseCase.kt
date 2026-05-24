package com.santiago.soberpath.domain.usecase

import com.santiago.soberpath.domain.repository.ConfigRepository

class RefreshRemoteConfigUseCase(
    private val configRepository: ConfigRepository
) {
    suspend operator fun invoke(): Boolean {
        return configRepository.refreshRemoteConfig()
    }
}


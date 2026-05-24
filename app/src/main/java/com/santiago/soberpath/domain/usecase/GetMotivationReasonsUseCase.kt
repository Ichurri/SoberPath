package com.santiago.soberpath.domain.usecase

import com.santiago.soberpath.domain.model.MotivationReason
import com.santiago.soberpath.domain.repository.MotivationRepository
import kotlinx.coroutines.flow.Flow

class GetMotivationReasonsUseCase(
    private val motivationRepository: MotivationRepository
) {
    operator fun invoke(habitId: String): Flow<List<MotivationReason>> {
        return motivationRepository.getMotivationReasons(habitId)
    }
}


package com.santiago.soberpath.domain.usecase

import com.santiago.soberpath.domain.model.MotivationReason
import com.santiago.soberpath.domain.repository.MotivationRepository

class AddMotivationReasonUseCase(
    private val motivationRepository: MotivationRepository
) {
    suspend operator fun invoke(reason: MotivationReason): MotivationReason {
        return motivationRepository.addMotivationReason(reason)
    }
}


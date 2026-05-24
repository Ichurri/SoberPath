package com.santiago.soberpath.domain.usecase

import com.santiago.soberpath.domain.model.DailyCheckIn
import com.santiago.soberpath.domain.repository.CheckInRepository
import kotlinx.coroutines.flow.Flow

class GetDailyCheckInsUseCase(
    private val checkInRepository: CheckInRepository
) {
    operator fun invoke(habitId: String): Flow<List<DailyCheckIn>> {
        return checkInRepository.getDailyCheckIns(habitId)
    }
}


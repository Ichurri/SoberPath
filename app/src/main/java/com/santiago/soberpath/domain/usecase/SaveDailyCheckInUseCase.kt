package com.santiago.soberpath.domain.usecase

import com.santiago.soberpath.domain.model.DailyCheckIn
import com.santiago.soberpath.domain.repository.CheckInRepository

class SaveDailyCheckInUseCase(
    private val checkInRepository: CheckInRepository
) {
    suspend operator fun invoke(checkIn: DailyCheckIn): DailyCheckIn {
        return checkInRepository.saveDailyCheckIn(checkIn)
    }
}


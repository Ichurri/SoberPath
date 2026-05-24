package com.santiago.soberpath.domain.repository

import com.santiago.soberpath.domain.model.DailyCheckIn
import kotlinx.coroutines.flow.Flow

interface CheckInRepository {
    suspend fun saveDailyCheckIn(checkIn: DailyCheckIn): DailyCheckIn
    fun getDailyCheckIns(habitId: String): Flow<List<DailyCheckIn>>
}


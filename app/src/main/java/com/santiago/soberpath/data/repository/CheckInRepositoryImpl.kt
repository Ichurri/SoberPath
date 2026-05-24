package com.santiago.soberpath.data.repository

import com.santiago.soberpath.data.local.dao.DailyCheckInDao
import com.santiago.soberpath.data.mapper.toDomain
import com.santiago.soberpath.data.mapper.toEntity
import com.santiago.soberpath.domain.model.DailyCheckIn
import com.santiago.soberpath.domain.repository.CheckInRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CheckInRepositoryImpl(
    private val dailyCheckInDao: DailyCheckInDao
) : CheckInRepository {
    override suspend fun saveDailyCheckIn(checkIn: DailyCheckIn): DailyCheckIn {
        dailyCheckInDao.insert(checkIn.toEntity())
        return checkIn
    }

    override fun getDailyCheckIns(habitId: String): Flow<List<DailyCheckIn>> {
        return dailyCheckInDao.observeByHabitId(habitId).map { list ->
            list.map { it.toDomain() }
        }
    }
}


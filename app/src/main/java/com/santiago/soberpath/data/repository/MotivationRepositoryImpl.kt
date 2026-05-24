package com.santiago.soberpath.data.repository

import com.santiago.soberpath.data.local.dao.MotivationReasonDao
import com.santiago.soberpath.data.mapper.toDomain
import com.santiago.soberpath.data.mapper.toEntity
import com.santiago.soberpath.domain.model.MotivationReason
import com.santiago.soberpath.domain.repository.MotivationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MotivationRepositoryImpl(
    private val motivationReasonDao: MotivationReasonDao
) : MotivationRepository {
    override suspend fun addMotivationReason(reason: MotivationReason): MotivationReason {
        motivationReasonDao.insert(reason.toEntity())
        return reason
    }

    override fun getMotivationReasons(habitId: String): Flow<List<MotivationReason>> {
        return motivationReasonDao.observeByHabitId(habitId).map { list ->
            list.map { it.toDomain() }
        }
    }
}


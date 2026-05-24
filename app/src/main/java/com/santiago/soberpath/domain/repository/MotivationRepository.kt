package com.santiago.soberpath.domain.repository

import com.santiago.soberpath.domain.model.MotivationReason
import kotlinx.coroutines.flow.Flow

interface MotivationRepository {
    suspend fun addMotivationReason(reason: MotivationReason): MotivationReason
    fun getMotivationReasons(habitId: String): Flow<List<MotivationReason>>
}


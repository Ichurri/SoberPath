package com.santiago.soberpath.domain.repository

import com.santiago.soberpath.domain.model.Relapse
import kotlinx.coroutines.flow.Flow

interface RelapseRepository {
    suspend fun createRelapse(relapse: Relapse)
    fun getRelapses(habitId: String): Flow<List<Relapse>>
}
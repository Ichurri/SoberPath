package com.santiago.soberpath.data.repository

import com.santiago.soberpath.data.local.dao.RelapseDao
import com.santiago.soberpath.data.mapper.toDomain
import com.santiago.soberpath.data.mapper.toEntity
import com.santiago.soberpath.domain.model.Relapse
import com.santiago.soberpath.domain.repository.RelapseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RelapseRepositoryImpl(
    private val relapseDao: RelapseDao
) : RelapseRepository {

    override suspend fun createRelapse(relapse: Relapse) {
        relapseDao.insert(relapse.toEntity())
    }

    override fun getRelapses(habitId: String): Flow<List<Relapse>> {
        return relapseDao.observeByHabitId(habitId).map { entities ->
            entities.map { it.toDomain() }
        }
    }
}
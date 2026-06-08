package com.santiago.soberpath.domain.usecase

import com.santiago.soberpath.domain.model.Relapse
import com.santiago.soberpath.domain.repository.RelapseRepository
import kotlinx.coroutines.flow.Flow

class GetRelapsesUseCase(
    private val relapseRepository: RelapseRepository
) {
    operator fun invoke(habitId: String): Flow<List<Relapse>> {
        return relapseRepository.getRelapses(habitId)
    }
}
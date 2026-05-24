package com.santiago.soberpath.domain.usecase

import com.santiago.soberpath.domain.model.Milestone
import com.santiago.soberpath.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow

class GetMilestonesUseCase(
    private val habitRepository: HabitRepository
) {
    operator fun invoke(habitId: String): Flow<List<Milestone>> {
        return habitRepository.getMilestones(habitId)
    }
}


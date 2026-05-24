package com.santiago.soberpath.domain.usecase

import com.santiago.soberpath.domain.model.SobrietyProgress
import com.santiago.soberpath.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow

class GetSobrietyProgressUseCase(
    private val habitRepository: HabitRepository
) {
    operator fun invoke(habitId: String): Flow<SobrietyProgress> {
        return habitRepository.getSobrietyProgress(habitId)
    }
}


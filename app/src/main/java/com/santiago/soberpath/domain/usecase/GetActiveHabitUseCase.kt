package com.santiago.soberpath.domain.usecase

import com.santiago.soberpath.domain.model.Habit
import com.santiago.soberpath.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow

class GetActiveHabitUseCase(
    private val habitRepository: HabitRepository
) {
    operator fun invoke(): Flow<Habit?> = habitRepository.getActiveHabit()
}


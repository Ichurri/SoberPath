package com.santiago.soberpath.domain.usecase

import com.santiago.soberpath.domain.model.Habit
import com.santiago.soberpath.domain.repository.HabitRepository

class SetActiveHabitUseCase(
    private val habitRepository: HabitRepository
) {
    suspend operator fun invoke(habitId: String): Habit? {
        return habitRepository.setActiveHabit(habitId)
    }
}
package com.santiago.soberpath.domain.usecase

import com.santiago.soberpath.domain.model.Habit
import com.santiago.soberpath.domain.repository.HabitRepository

class CreateHabitUseCase(
    private val habitRepository: HabitRepository
) {
    suspend operator fun invoke(habit: Habit): Habit = habitRepository.createHabit(habit)
}


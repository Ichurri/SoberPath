package com.santiago.soberpath.domain.usecase

import com.santiago.soberpath.domain.model.Habit
import com.santiago.soberpath.domain.repository.HabitRepository
import java.time.LocalDate

class RegisterRelapseUseCase(
    private val habitRepository: HabitRepository
) {
    suspend operator fun invoke(habitId: String, relapseDate: LocalDate): Habit? {
        return habitRepository.registerRelapse(habitId, relapseDate)
    }
}


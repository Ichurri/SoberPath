package com.santiago.soberpath.domain.usecase

import com.santiago.soberpath.domain.repository.HabitRepository

class DeleteHabitUseCase(
    private val habitRepository: HabitRepository
) {
    suspend operator fun invoke(habitId: String) {
        habitRepository.deleteHabit(habitId)
    }
}

package com.santiago.soberpath.domain.usecase

import com.santiago.soberpath.domain.model.Habit
import com.santiago.soberpath.domain.model.Relapse
import com.santiago.soberpath.domain.repository.HabitRepository
import com.santiago.soberpath.domain.repository.RelapseRepository
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

class RegisterRelapseUseCase(
    private val habitRepository: HabitRepository,
    private val relapseRepository: RelapseRepository
) {
    suspend operator fun invoke(
        habitId: String,
        relapseDate: LocalDate,
        cravingLevel: Int = 0,
        trigger: String = "",
        note: String = ""
    ): Habit? {
        val updatedHabit = habitRepository.registerRelapse(
            habitId = habitId,
            relapseDate = relapseDate
        )

        if (updatedHabit != null) {
            val relapse = Relapse(
                id = UUID.randomUUID().toString(),
                habitId = habitId,
                relapseDate = relapseDate,
                cravingLevel = cravingLevel,
                trigger = trigger,
                note = note,
                createdAt = LocalDateTime.now()
            )

            relapseRepository.createRelapse(relapse)
        }

        return updatedHabit
    }
}
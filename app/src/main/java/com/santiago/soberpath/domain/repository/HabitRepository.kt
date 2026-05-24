package com.santiago.soberpath.domain.repository

import com.santiago.soberpath.domain.model.Habit
import com.santiago.soberpath.domain.model.Milestone
import com.santiago.soberpath.domain.model.SobrietyProgress
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    suspend fun createHabit(habit: Habit): Habit
    fun getActiveHabit(): Flow<Habit?>
    suspend fun registerRelapse(habitId: String, relapseDate: LocalDate): Habit?
    fun getSobrietyProgress(habitId: String): Flow<SobrietyProgress>
    fun getMilestones(habitId: String): Flow<List<Milestone>>
}


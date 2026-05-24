package com.santiago.soberpath.data.repository

import com.santiago.soberpath.data.local.dao.HabitDao
import com.santiago.soberpath.data.local.dao.MilestoneDao
import com.santiago.soberpath.data.mapper.toDomain
import com.santiago.soberpath.data.mapper.toEntity
import com.santiago.soberpath.domain.model.Habit
import com.santiago.soberpath.domain.model.Milestone
import com.santiago.soberpath.domain.model.SobrietyProgress
import com.santiago.soberpath.domain.repository.HabitRepository
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HabitRepositoryImpl(
    private val habitDao: HabitDao,
    private val milestoneDao: MilestoneDao
) : HabitRepository {
    override suspend fun createHabit(habit: Habit): Habit {
        habitDao.upsert(habit.toEntity())
        return habit
    }

    override fun getActiveHabit(): Flow<Habit?> {
        return habitDao.observeActiveHabit().map { entity -> entity?.toDomain() }
    }

    override suspend fun registerRelapse(habitId: String, relapseDate: LocalDate): Habit? {
        val existing = habitDao.getById(habitId) ?: return null
        val updated = existing.copy(lastRelapseDate = relapseDate.toString())
        habitDao.upsert(updated)
        return updated.toDomain()
    }

    override fun getSobrietyProgress(habitId: String): Flow<SobrietyProgress> {
        return habitDao.observeById(habitId).map { entity ->
            if (entity == null) {
                SobrietyProgress(0, 0, 0, 0, 0.0)
            } else {
                entity.toProgress(LocalDateTime.now())
            }
        }
    }

    override fun getMilestones(habitId: String): Flow<List<Milestone>> {
        return milestoneDao.observeAll().map { list -> list.map { it.toDomain() } }
    }

    private fun com.santiago.soberpath.data.local.entity.HabitEntity.toProgress(
        now: LocalDateTime
    ): SobrietyProgress {
        val relapseAtStart = LocalDate.parse(lastRelapseDate).atStartOfDay()
        val duration = if (now.isBefore(relapseAtStart)) {
            Duration.ZERO
        } else {
            Duration.between(relapseAtStart, now)
        }
        val totalMinutes = duration.toMinutes()
        val days = totalMinutes / (60 * 24)
        val hours = (totalMinutes / 60) % 24
        val minutes = totalMinutes % 60
        val savedAmount = days * dailyCost
        return SobrietyProgress(totalMinutes, days, hours, minutes, savedAmount)
    }
}


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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class HabitRepositoryImpl(
    private val habitDao: HabitDao,
    private val milestoneDao: MilestoneDao
) : HabitRepository {

    override suspend fun createHabit(habit: Habit): Habit {
        if (habit.isActive) {
            habitDao.deactivateAllHabits()
        }

        habitDao.upsert(habit.toEntity())
        return habit
    }

    override fun getAllHabits(): Flow<List<Habit>> {
        return habitDao.observeAllHabits().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getActiveHabit(): Flow<Habit?> {
        return habitDao.observeActiveHabit().map { entity ->
            entity?.toDomain()
        }
    }

    override suspend fun setActiveHabit(habitId: String): Habit? {
        val existing = habitDao.getById(habitId) ?: return null

        habitDao.deactivateAllHabits()
        habitDao.activateHabit(habitId)

        return existing.copy(isActive = true).toDomain()
    }

    override suspend fun registerRelapse(habitId: String, relapseDateTime: LocalDateTime): Habit? {
        val existing = habitDao.getById(habitId) ?: return null
        val updated = existing.copy(lastRelapseDate = relapseDateTime.toString())
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
        return combine(
            habitDao.observeById(habitId),
            milestoneDao.observeAll()
        ) { habitEntity, milestones ->
            val daysSinceRelapse = habitEntity?.let { entity ->
                val relapseAtStart = try {
                    if (entity.lastRelapseDate.contains("T")) {
                        LocalDateTime.parse(entity.lastRelapseDate)
                    } else {
                        LocalDate.parse(entity.lastRelapseDate).atStartOfDay()
                    }
                } catch (e: Exception) {
                    LocalDateTime.now()
                }
                val duration = if (LocalDateTime.now().isBefore(relapseAtStart)) {
                    Duration.ZERO
                } else {
                    Duration.between(relapseAtStart, LocalDateTime.now())
                }
                duration.toDays()
            } ?: 0

            milestones.map { milestone ->
                milestone.toDomain().copy(
                    achieved = daysSinceRelapse >= milestone.daysRequired
                )
            }
        }
    }

    override suspend fun deleteHabit(habitId: String) {
        val existing = habitDao.getById(habitId) ?: return
        habitDao.deleteById(habitId)

        if (existing.isActive) {
            val remaining = habitDao.getAllHabitsList()
            val nextActive = remaining.firstOrNull()
            if (nextActive != null) {
                habitDao.activateHabit(nextActive.id)
            }
        }
    }

    private fun com.santiago.soberpath.data.local.entity.HabitEntity.toProgress(
        now: LocalDateTime
    ): SobrietyProgress {
        val relapseAtStart = try {
            if (lastRelapseDate.contains("T")) {
                LocalDateTime.parse(lastRelapseDate)
            } else {
                LocalDate.parse(lastRelapseDate).atStartOfDay()
            }
        } catch (e: Exception) {
            now
        }

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
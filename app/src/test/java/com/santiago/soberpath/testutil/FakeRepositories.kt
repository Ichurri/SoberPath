package com.santiago.soberpath.testutil

import com.santiago.soberpath.domain.model.AppConfig
import com.santiago.soberpath.domain.model.DailyCheckIn
import com.santiago.soberpath.domain.model.Habit
import com.santiago.soberpath.domain.model.Milestone
import com.santiago.soberpath.domain.model.Relapse
import com.santiago.soberpath.domain.model.SobrietyProgress
import com.santiago.soberpath.domain.repository.CheckInRepository
import com.santiago.soberpath.domain.repository.ConfigRepository
import com.santiago.soberpath.domain.repository.HabitRepository
import com.santiago.soberpath.domain.repository.RelapseRepository
import java.time.LocalDateTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Implementaciones en memoria de los repositorios de dominio para tests unitarios.
 * Cada fake graba las llamadas relevantes y expone flujos mutables para simular datos.
 */
class FakeHabitRepository : HabitRepository {

    val activeHabit = MutableStateFlow<Habit?>(null)
    val allHabits = MutableStateFlow<List<Habit>>(emptyList())
    val sobrietyProgress = MutableStateFlow(
        SobrietyProgress(
            totalMinutesSinceRelapse = 0,
            days = 0,
            hours = 0,
            minutes = 0,
            savedAmount = 0.0
        )
    )
    val milestones = MutableStateFlow<List<Milestone>>(emptyList())

    val createdHabits = mutableListOf<Habit>()
    var createHabitError: Throwable? = null

    var registerRelapseResult: Habit? = null
    var registerRelapseHabitId: String? = null
    var registerRelapseDateTime: LocalDateTime? = null

    var deletedHabitId: String? = null
    var setActiveResult: Habit? = null
    var setActiveHabitId: String? = null

    override suspend fun createHabit(habit: Habit): Habit {
        createHabitError?.let { throw it }
        createdHabits += habit
        activeHabit.value = habit
        return habit
    }

    override fun getActiveHabit(): Flow<Habit?> = activeHabit

    override fun getAllHabits(): Flow<List<Habit>> = allHabits

    override suspend fun setActiveHabit(habitId: String): Habit? {
        setActiveHabitId = habitId
        return setActiveResult
    }

    override suspend fun registerRelapse(habitId: String, relapseDateTime: LocalDateTime): Habit? {
        registerRelapseHabitId = habitId
        registerRelapseDateTime = relapseDateTime
        return registerRelapseResult
    }

    override fun getSobrietyProgress(habitId: String): Flow<SobrietyProgress> = sobrietyProgress

    override fun getMilestones(habitId: String): Flow<List<Milestone>> = milestones

    override suspend fun deleteHabit(habitId: String) {
        deletedHabitId = habitId
    }
}

class FakeRelapseRepository : RelapseRepository {
    val created = mutableListOf<Relapse>()
    val relapses = MutableStateFlow<List<Relapse>>(emptyList())

    override suspend fun createRelapse(relapse: Relapse) {
        created += relapse
    }

    override fun getRelapses(habitId: String): Flow<List<Relapse>> = relapses
}

class FakeCheckInRepository : CheckInRepository {
    val saved = mutableListOf<DailyCheckIn>()
    val checkIns = MutableStateFlow<List<DailyCheckIn>>(emptyList())
    var saveError: Throwable? = null

    override suspend fun saveDailyCheckIn(checkIn: DailyCheckIn): DailyCheckIn {
        saveError?.let { throw it }
        saved += checkIn
        return checkIn
    }

    override fun getDailyCheckIns(habitId: String): Flow<List<DailyCheckIn>> = checkIns
}

class FakeConfigRepository : ConfigRepository {
    val config = MutableStateFlow(defaultAppConfig())
    var refreshResult: Boolean = true

    override fun getRemoteConfig(): Flow<AppConfig> = config

    override suspend fun refreshRemoteConfig(): Boolean = refreshResult

    companion object {
        fun defaultAppConfig() = AppConfig(
            dailyReminderEnabled = false,
            dailyReminderHour = 20,
            remoteMessage = "",
            emergencyTipsEnabled = false,
            minSupportedVersion = 1,
            motivationalQuote = "",
            showMilestoneAnimation = false,
            checkinRequired = false,
            onboardingConfig = emptyList()
        )
    }
}

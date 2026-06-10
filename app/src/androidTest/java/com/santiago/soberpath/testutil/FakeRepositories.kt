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

/** Fakes en memoria para tests instrumentados (UI). Espejo de los de `src/test`. */
class FakeHabitRepository : HabitRepository {
    val activeHabit = MutableStateFlow<Habit?>(null)
    val allHabits = MutableStateFlow<List<Habit>>(emptyList())
    val sobrietyProgress = MutableStateFlow(
        SobrietyProgress(0, 0, 0, 0, 0.0)
    )
    val milestones = MutableStateFlow<List<Milestone>>(emptyList())
    val createdHabits = mutableListOf<Habit>()

    override suspend fun createHabit(habit: Habit): Habit {
        createdHabits += habit
        activeHabit.value = habit
        return habit
    }

    override fun getActiveHabit(): Flow<Habit?> = activeHabit
    override fun getAllHabits(): Flow<List<Habit>> = allHabits
    override suspend fun setActiveHabit(habitId: String): Habit? = null
    override suspend fun registerRelapse(habitId: String, relapseDateTime: LocalDateTime): Habit? = null
    override fun getSobrietyProgress(habitId: String): Flow<SobrietyProgress> = sobrietyProgress
    override fun getMilestones(habitId: String): Flow<List<Milestone>> = milestones
    override suspend fun deleteHabit(habitId: String) = Unit
}

class FakeRelapseRepository : RelapseRepository {
    val relapses = MutableStateFlow<List<Relapse>>(emptyList())
    override suspend fun createRelapse(relapse: Relapse) = Unit
    override fun getRelapses(habitId: String): Flow<List<Relapse>> = relapses
}

class FakeCheckInRepository : CheckInRepository {
    val checkIns = MutableStateFlow<List<DailyCheckIn>>(emptyList())
    override suspend fun saveDailyCheckIn(checkIn: DailyCheckIn): DailyCheckIn = checkIn
    override fun getDailyCheckIns(habitId: String): Flow<List<DailyCheckIn>> = checkIns
}

class FakeConfigRepository : ConfigRepository {
    val config = MutableStateFlow(
        AppConfig(
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
    )

    override fun getRemoteConfig(): Flow<AppConfig> = config
    override suspend fun refreshRemoteConfig(): Boolean = true
}

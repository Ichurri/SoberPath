package com.santiago.soberpath.domain.usecase

import com.santiago.soberpath.domain.model.SobrietyProgress
import com.santiago.soberpath.testutil.FakeHabitRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetSobrietyProgressUseCaseTest {

    @Test
    fun `emits the progress provided by the repository`() = runTest {
        val repository = FakeHabitRepository()
        val expected = SobrietyProgress(
            totalMinutesSinceRelapse = 2880,
            days = 2,
            hours = 0,
            minutes = 0,
            savedAmount = 40.0
        )
        repository.sobrietyProgress.value = expected
        val useCase = GetSobrietyProgressUseCase(repository)

        val result = useCase("habit-1").first()

        assertEquals(expected, result)
    }

    @Test
    fun `reflects subsequent updates from the repository`() = runTest {
        val repository = FakeHabitRepository()
        val useCase = GetSobrietyProgressUseCase(repository)

        val updated = SobrietyProgress(
            totalMinutesSinceRelapse = 4320,
            days = 3,
            hours = 0,
            minutes = 0,
            savedAmount = 60.0
        )
        repository.sobrietyProgress.value = updated

        assertEquals(3, useCase("habit-1").first().days)
        assertEquals(60.0, useCase("habit-1").first().savedAmount, 0.001)
    }
}

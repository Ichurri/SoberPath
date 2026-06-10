package com.santiago.soberpath.domain.usecase

import com.santiago.soberpath.testutil.FakeHabitRepository
import com.santiago.soberpath.testutil.FakeRelapseRepository
import com.santiago.soberpath.testutil.testHabit
import java.time.LocalDate
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RegisterRelapseUseCaseTest {

    private fun useCase(
        habitRepository: FakeHabitRepository,
        relapseRepository: FakeRelapseRepository
    ) = RegisterRelapseUseCase(habitRepository, relapseRepository)

    @Test
    fun `registers relapse and stores its details when habit exists`() = runTest {
        val habitRepo = FakeHabitRepository().apply { registerRelapseResult = testHabit() }
        val relapseRepo = FakeRelapseRepository()
        val pastDate = LocalDate.of(2026, 5, 20)

        val result = useCase(habitRepo, relapseRepo)(
            habitId = "habit-1",
            relapseDate = pastDate,
            cravingLevel = 4,
            trigger = "Fiesta",
            note = "recaída"
        )

        // El hábito actualizado se devuelve
        assertEquals("habit-1", result?.id)
        // Se delega al repositorio de hábitos con el id correcto
        assertEquals("habit-1", habitRepo.registerRelapseHabitId)
        // Para una fecha pasada se usa el inicio del día
        assertEquals(pastDate.atStartOfDay(), habitRepo.registerRelapseDateTime)
        // Se persiste un registro de recaída con los datos del formulario
        assertEquals(1, relapseRepo.created.size)
        val stored = relapseRepo.created.first()
        assertEquals("habit-1", stored.habitId)
        assertEquals(pastDate, stored.relapseDate)
        assertEquals(4, stored.cravingLevel)
        assertEquals("Fiesta", stored.trigger)
        assertEquals("recaída", stored.note)
    }

    @Test
    fun `does not store relapse when habit is not found`() = runTest {
        val habitRepo = FakeHabitRepository().apply { registerRelapseResult = null }
        val relapseRepo = FakeRelapseRepository()

        val result = useCase(habitRepo, relapseRepo)(
            habitId = "missing",
            relapseDate = LocalDate.of(2026, 5, 20)
        )

        assertNull(result)
        assertTrue(relapseRepo.created.isEmpty())
    }

    @Test
    fun `uses current time when relapse date is today`() = runTest {
        val habitRepo = FakeHabitRepository().apply { registerRelapseResult = testHabit() }
        val relapseRepo = FakeRelapseRepository()
        val today = LocalDate.now()

        useCase(habitRepo, relapseRepo)(habitId = "habit-1", relapseDate = today)

        val usedDateTime = habitRepo.registerRelapseDateTime!!
        // Para hoy se usa la hora actual, no el inicio del día
        assertEquals(today, usedDateTime.toLocalDate())
        assertTrue(usedDateTime.isAfter(today.atStartOfDay()))
    }
}

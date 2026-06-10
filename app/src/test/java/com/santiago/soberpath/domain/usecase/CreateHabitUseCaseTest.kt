package com.santiago.soberpath.domain.usecase

import com.santiago.soberpath.testutil.FakeHabitRepository
import com.santiago.soberpath.testutil.testHabit
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class CreateHabitUseCaseTest {

    @Test
    fun `creates habit through repository and returns it`() = runTest {
        val repository = FakeHabitRepository()
        val useCase = CreateHabitUseCase(repository)
        val habit = testHabit(name = "Cigarro")

        val result = useCase(habit)

        assertEquals(1, repository.createdHabits.size)
        assertEquals("Cigarro", repository.createdHabits.first().name)
        assertSame(habit, result)
    }

    @Test
    fun `marks created habit as the active one`() = runTest {
        val repository = FakeHabitRepository()
        val useCase = CreateHabitUseCase(repository)
        val habit = testHabit(id = "habit-42")

        useCase(habit)

        assertEquals("habit-42", repository.activeHabit.value?.id)
    }

    @Test
    fun `propagates repository errors`() = runTest {
        val repository = FakeHabitRepository().apply {
            createHabitError = IllegalStateException("db error")
        }
        val useCase = CreateHabitUseCase(repository)

        try {
            useCase(testHabit())
            fail("Expected IllegalStateException")
        } catch (e: IllegalStateException) {
            assertTrue(e.message == "db error")
        }
    }
}

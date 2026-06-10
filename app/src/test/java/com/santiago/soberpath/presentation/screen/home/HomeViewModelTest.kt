package com.santiago.soberpath.presentation.screen.home

import com.santiago.soberpath.domain.usecase.GetActiveHabitUseCase
import com.santiago.soberpath.domain.usecase.GetDailyCheckInsUseCase
import com.santiago.soberpath.domain.usecase.GetRelapsesUseCase
import com.santiago.soberpath.domain.usecase.GetRemoteConfigUseCase
import com.santiago.soberpath.domain.usecase.GetSobrietyProgressUseCase
import com.santiago.soberpath.testutil.FakeCheckInRepository
import com.santiago.soberpath.testutil.FakeConfigRepository
import com.santiago.soberpath.testutil.FakeHabitRepository
import com.santiago.soberpath.testutil.FakeRelapseRepository
import com.santiago.soberpath.testutil.MainDispatcherRule
import com.santiago.soberpath.testutil.testHabit
import com.santiago.soberpath.testutil.testRelapse
import java.time.LocalDate
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val habitRepository = FakeHabitRepository()
    private val relapseRepository = FakeRelapseRepository()
    private val checkInRepository = FakeCheckInRepository()
    private val configRepository = FakeConfigRepository()

    private fun newViewModel() = HomeViewModel(
        GetActiveHabitUseCase(habitRepository),
        GetSobrietyProgressUseCase(habitRepository),
        GetRemoteConfigUseCase(configRepository),
        GetDailyCheckInsUseCase(checkInRepository),
        GetRelapsesUseCase(relapseRepository)
    )

    @Test
    fun `with no active habit shows empty state`() = runTest {
        val viewModel = newViewModel()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertFalse(state.hasHabit)
        assertEquals(0, state.relapseCount)
    }

    // Nota: este test NO usa runTest a propósito. Con un hábito activo, HomeViewModel arranca un
    // ticker `while(true){ delay(1000) }`; como runTest reutiliza el scheduler del Main de tipo
    // TestDispatcher, su auto-avance entraría en ese bucle infinito. Con el MainDispatcherRule
    // (UnconfinedTestDispatcher) la inicialización corre de forma eager y el estado queda listo
    // tras construir el ViewModel, sin necesidad de avanzar el tiempo virtual.
    @Test
    fun `active habit populates name progress and relapses`() {
        habitRepository.activeHabit.value = testHabit(name = "Cigarro")
        relapseRepository.relapses.value = listOf(
            testRelapse(id = "r1", relapseDate = LocalDate.of(2026, 6, 1)),
            testRelapse(id = "r2", relapseDate = LocalDate.of(2026, 5, 1))
        )

        val viewModel = newViewModel()

        val state = viewModel.state.value
        assertTrue(state.hasHabit)
        assertEquals("Cigarro", state.habitName)
        assertEquals(2, state.relapseCount)
        assertTrue(state.lastRelapseDate.isNotBlank())
        assertTrue(state.timeSinceRelapse.isNotBlank())
    }

    @Test
    fun `daily check-in intent emits navigation effect`() = runTest {
        val viewModel = newViewModel()
        val effects = mutableListOf<HomeContract.UiEffect>()
        backgroundScope.launch(mainDispatcherRule.testDispatcher) {
            viewModel.effect.collect { effects += it }
        }

        viewModel.onIntent(HomeContract.UiIntent.DailyCheckInClicked)

        assertTrue(effects.any { it == HomeContract.UiEffect.NavigateDailyCheckIn })
    }

    @Test
    fun `settings intent emits navigation effect`() = runTest {
        val viewModel = newViewModel()
        val effects = mutableListOf<HomeContract.UiEffect>()
        backgroundScope.launch(mainDispatcherRule.testDispatcher) {
            viewModel.effect.collect { effects += it }
        }

        viewModel.onIntent(HomeContract.UiIntent.SettingsClicked)

        assertTrue(effects.any { it == HomeContract.UiEffect.NavigateSettings })
    }

    @Test
    fun `remote config motivational quote is reflected in state`() = runTest {
        configRepository.config.value = FakeConfigRepository.defaultAppConfig()
            .copy(motivationalQuote = "Un día a la vez")

        val viewModel = newViewModel()

        assertEquals("Un día a la vez", viewModel.state.value.motivationalMessage)
    }
}

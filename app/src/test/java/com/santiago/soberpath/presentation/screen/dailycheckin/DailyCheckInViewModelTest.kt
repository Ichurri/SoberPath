package com.santiago.soberpath.presentation.screen.dailycheckin

import com.santiago.soberpath.R
import com.santiago.soberpath.domain.usecase.GetActiveHabitUseCase
import com.santiago.soberpath.domain.usecase.SaveDailyCheckInUseCase
import com.santiago.soberpath.presentation.util.UiText
import com.santiago.soberpath.testutil.FakeCheckInRepository
import com.santiago.soberpath.testutil.FakeHabitRepository
import com.santiago.soberpath.testutil.MainDispatcherRule
import com.santiago.soberpath.testutil.testHabit
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class DailyCheckInViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun newViewModel(
        habitRepository: FakeHabitRepository,
        checkInRepository: FakeCheckInRepository
    ) = DailyCheckInViewModel(
        GetActiveHabitUseCase(habitRepository),
        SaveDailyCheckInUseCase(checkInRepository)
    )

    private fun resId(effect: DailyCheckInContract.UiEffect.ShowMessage): Int =
        (effect.message as UiText.StringResource).resId

    @Test
    fun `save without active habit shows no-active-habit message`() = runTest {
        val habitRepository = FakeHabitRepository() // no active habit
        val checkInRepository = FakeCheckInRepository()
        val viewModel = newViewModel(habitRepository, checkInRepository)
        val effects = mutableListOf<DailyCheckInContract.UiEffect>()
        backgroundScope.launch(mainDispatcherRule.testDispatcher) {
            viewModel.effect.collect { effects += it }
        }

        viewModel.onIntent(DailyCheckInContract.UiIntent.UpdateMood("Feliz"))
        viewModel.onIntent(DailyCheckInContract.UiIntent.Save)

        assertTrue(checkInRepository.saved.isEmpty())
        val message = effects.filterIsInstance<DailyCheckInContract.UiEffect.ShowMessage>().last()
        assertEquals(R.string.message_no_active_habit, resId(message))
    }

    @Test
    fun `save with blank mood shows required-fields message`() = runTest {
        val habitRepository = FakeHabitRepository().apply { activeHabit.value = testHabit() }
        val checkInRepository = FakeCheckInRepository()
        val viewModel = newViewModel(habitRepository, checkInRepository)
        val effects = mutableListOf<DailyCheckInContract.UiEffect>()
        backgroundScope.launch(mainDispatcherRule.testDispatcher) {
            viewModel.effect.collect { effects += it }
        }

        viewModel.onIntent(DailyCheckInContract.UiIntent.Save)

        assertTrue(checkInRepository.saved.isEmpty())
        val message = effects.filterIsInstance<DailyCheckInContract.UiEffect.ShowMessage>().last()
        assertEquals(R.string.error_required_fields, resId(message))
    }

    @Test
    fun `valid check-in is saved and navigates back`() = runTest {
        val habitRepository = FakeHabitRepository().apply { activeHabit.value = testHabit(id = "h-9") }
        val checkInRepository = FakeCheckInRepository()
        val viewModel = newViewModel(habitRepository, checkInRepository)
        val effects = mutableListOf<DailyCheckInContract.UiEffect>()
        backgroundScope.launch(mainDispatcherRule.testDispatcher) {
            viewModel.effect.collect { effects += it }
        }

        viewModel.onIntent(DailyCheckInContract.UiIntent.UpdateMood("Tranquilo"))
        viewModel.onIntent(DailyCheckInContract.UiIntent.UpdateCraving(4))
        viewModel.onIntent(DailyCheckInContract.UiIntent.UpdatePledge(true))
        viewModel.onIntent(DailyCheckInContract.UiIntent.Save)

        assertEquals(1, checkInRepository.saved.size)
        val saved = checkInRepository.saved.first()
        assertEquals("h-9", saved.habitId)
        assertEquals("Tranquilo", saved.mood)
        assertEquals(4, saved.cravingLevel)
        assertEquals(true, saved.completedPledge)
        assertTrue(effects.any { it is DailyCheckInContract.UiEffect.NavigateBack })
    }

    @Test
    fun `intents update the form state`() = runTest {
        val viewModel = newViewModel(FakeHabitRepository(), FakeCheckInRepository())

        viewModel.onIntent(DailyCheckInContract.UiIntent.UpdateMood("Ansioso"))
        viewModel.onIntent(DailyCheckInContract.UiIntent.UpdateCraving(5))
        viewModel.onIntent(DailyCheckInContract.UiIntent.UpdateNote("nota"))

        val state = viewModel.state.value
        assertEquals("Ansioso", state.mood)
        assertEquals(5, state.cravingLevel)
        assertEquals("nota", state.note)
    }
}

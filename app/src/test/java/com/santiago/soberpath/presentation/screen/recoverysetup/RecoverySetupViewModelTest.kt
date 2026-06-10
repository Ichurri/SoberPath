package com.santiago.soberpath.presentation.screen.recoverysetup

import com.santiago.soberpath.domain.usecase.CreateHabitUseCase
import com.santiago.soberpath.testutil.FakeHabitRepository
import com.santiago.soberpath.testutil.MainDispatcherRule
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class RecoverySetupViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun newViewModel(repository: FakeHabitRepository) =
        RecoverySetupViewModel(CreateHabitUseCase(repository))

    @Test
    fun `save with blank name sets name error and does not create habit`() = runTest {
        val repository = FakeHabitRepository()
        val viewModel = newViewModel(repository)
        val effects = mutableListOf<RecoverySetupContract.UiEffect>()
        backgroundScope.launch(mainDispatcherRule.testDispatcher) {
            viewModel.effect.collect { effects += it }
        }

        viewModel.onIntent(RecoverySetupContract.UiIntent.SaveClicked)

        assertNotNull(viewModel.state.value.nameError)
        assertTrue(repository.createdHabits.isEmpty())
        assertTrue(effects.none { it is RecoverySetupContract.UiEffect.NavigateHome })
    }

    @Test
    fun `save with negative cost sets cost error only`() = runTest {
        val repository = FakeHabitRepository()
        val viewModel = newViewModel(repository)

        viewModel.onIntent(RecoverySetupContract.UiIntent.HabitNameChanged("Alcohol"))
        viewModel.onIntent(RecoverySetupContract.UiIntent.DailyCostChanged("-5"))
        viewModel.onIntent(RecoverySetupContract.UiIntent.SaveClicked)

        assertNotNull(viewModel.state.value.costError)
        assertNull(viewModel.state.value.nameError)
        assertTrue(repository.createdHabits.isEmpty())
    }

    @Test
    fun `valid data creates trimmed habit and navigates home`() = runTest {
        val repository = FakeHabitRepository()
        val viewModel = newViewModel(repository)
        val effects = mutableListOf<RecoverySetupContract.UiEffect>()
        backgroundScope.launch(mainDispatcherRule.testDispatcher) {
            viewModel.effect.collect { effects += it }
        }

        viewModel.onIntent(RecoverySetupContract.UiIntent.HabitNameChanged("  Alcohol  "))
        viewModel.onIntent(RecoverySetupContract.UiIntent.DailyCostChanged("15,5"))
        viewModel.onIntent(RecoverySetupContract.UiIntent.CurrencyChanged("USD"))
        viewModel.onIntent(RecoverySetupContract.UiIntent.SaveClicked)

        assertEquals(1, repository.createdHabits.size)
        val created = repository.createdHabits.first()
        assertEquals("Alcohol", created.name)
        assertEquals(15.5, created.dailyCost, 0.001)
        assertEquals("USD", created.currency)
        assertTrue(created.isActive)
        assertTrue(effects.any { it is RecoverySetupContract.UiEffect.NavigateHome })
        assertFalse(viewModel.state.value.isSaving)
    }

    @Test
    fun `blank cost is treated as zero and is valid`() = runTest {
        val repository = FakeHabitRepository()
        val viewModel = newViewModel(repository)

        viewModel.onIntent(RecoverySetupContract.UiIntent.HabitNameChanged("Cigarro"))
        viewModel.onIntent(RecoverySetupContract.UiIntent.SaveClicked)

        assertEquals(1, repository.createdHabits.size)
        assertEquals(0.0, repository.createdHabits.first().dailyCost, 0.001)
    }

    @Test
    fun `changing name clears a previous name error`() = runTest {
        val repository = FakeHabitRepository()
        val viewModel = newViewModel(repository)

        viewModel.onIntent(RecoverySetupContract.UiIntent.SaveClicked)
        assertNotNull(viewModel.state.value.nameError)

        viewModel.onIntent(RecoverySetupContract.UiIntent.HabitNameChanged("Apuestas"))
        assertNull(viewModel.state.value.nameError)
    }
}

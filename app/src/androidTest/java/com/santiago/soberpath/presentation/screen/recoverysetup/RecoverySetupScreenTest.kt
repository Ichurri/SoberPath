package com.santiago.soberpath.presentation.screen.recoverysetup

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.santiago.soberpath.R
import com.santiago.soberpath.domain.usecase.CreateHabitUseCase
import com.santiago.soberpath.testutil.FakeHabitRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecoverySetupScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun rendersHeaderAndSaveButton() {
        val repository = FakeHabitRepository()
        val viewModel = RecoverySetupViewModel(CreateHabitUseCase(repository))
        composeTestRule.setContent {
            RecoverySetupScreen(onNavigateHome = {}, viewModel = viewModel)
        }
        val context = composeTestRule.activity

        composeTestRule
            .onNodeWithText(context.getString(R.string.recovery_setup_header))
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText(context.getString(R.string.recovery_setup_save_button))
            .assertIsDisplayed()
    }

    @Test
    fun savingValidHabitNavigatesHome() {
        val repository = FakeHabitRepository()
        val viewModel = RecoverySetupViewModel(CreateHabitUseCase(repository))
        var navigatedHome = false
        composeTestRule.setContent {
            RecoverySetupScreen(onNavigateHome = { navigatedHome = true }, viewModel = viewModel)
        }
        val context = composeTestRule.activity

        composeTestRule.runOnUiThread {
            viewModel.onIntent(RecoverySetupContract.UiIntent.HabitNameChanged("Alcohol"))
            viewModel.onIntent(RecoverySetupContract.UiIntent.DailyCostChanged("10"))
        }

        composeTestRule
            .onNodeWithText(context.getString(R.string.recovery_setup_save_button))
            .performClick()

        composeTestRule.waitUntil(timeoutMillis = 5_000) { navigatedHome }
        assertTrue(navigatedHome)
        assertEquals(1, repository.createdHabits.size)
        assertEquals("Alcohol", repository.createdHabits.first().name)
    }

    @Test
    fun savingWithoutNameShowsErrorAndDoesNotNavigate() {
        val repository = FakeHabitRepository()
        val viewModel = RecoverySetupViewModel(CreateHabitUseCase(repository))
        var navigatedHome = false
        composeTestRule.setContent {
            RecoverySetupScreen(onNavigateHome = { navigatedHome = true }, viewModel = viewModel)
        }
        val context = composeTestRule.activity

        composeTestRule
            .onNodeWithText(context.getString(R.string.recovery_setup_save_button))
            .performClick()
        composeTestRule.waitForIdle()

        assertFalse(navigatedHome)
        assertTrue(repository.createdHabits.isEmpty())
        composeTestRule
            .onNodeWithText(context.getString(R.string.recovery_setup_error_name_required))
            .assertIsDisplayed()
    }
}

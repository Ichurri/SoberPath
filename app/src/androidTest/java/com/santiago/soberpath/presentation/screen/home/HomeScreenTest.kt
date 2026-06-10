package com.santiago.soberpath.presentation.screen.home

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.santiago.soberpath.R
import com.santiago.soberpath.domain.usecase.GetActiveHabitUseCase
import com.santiago.soberpath.domain.usecase.GetDailyCheckInsUseCase
import com.santiago.soberpath.domain.usecase.GetRelapsesUseCase
import com.santiago.soberpath.domain.usecase.GetRemoteConfigUseCase
import com.santiago.soberpath.domain.usecase.GetSobrietyProgressUseCase
import com.santiago.soberpath.testutil.FakeCheckInRepository
import com.santiago.soberpath.testutil.FakeConfigRepository
import com.santiago.soberpath.testutil.FakeHabitRepository
import com.santiago.soberpath.testutil.FakeRelapseRepository
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val habitRepository = FakeHabitRepository()

    private fun homeViewModel() = HomeViewModel(
        GetActiveHabitUseCase(habitRepository),
        GetSobrietyProgressUseCase(habitRepository),
        GetRemoteConfigUseCase(FakeConfigRepository()),
        GetDailyCheckInsUseCase(FakeCheckInRepository()),
        GetRelapsesUseCase(FakeRelapseRepository())
    )

    private fun setHome(viewModel: HomeViewModel, onRecoverySetup: () -> Unit = {}) {
        composeTestRule.setContent {
            HomeScreen(
                onDailyCheckIn = {},
                onMotivation = {},
                onMilestones = {},
                onSettings = {},
                onRecoverySetup = onRecoverySetup,
                onRegisterRelapse = {},
                onHabits = {},
                onRelapseHistory = {},
                viewModel = viewModel
            )
        }
    }

    @Test
    fun emptyState_showsEmptyTitle() {
        // Sin hábito activo (FakeHabitRepository por defecto devuelve null)
        setHome(homeViewModel())
        val title = composeTestRule.activity.getString(R.string.home_empty_title)

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText(title).fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText(title).assertIsDisplayed()
    }

    @Test
    fun emptyState_createTrackingButtonNavigates() {
        var navigatedRecoverySetup = false
        setHome(homeViewModel(), onRecoverySetup = { navigatedRecoverySetup = true })
        val buttonText = composeTestRule.activity.getString(R.string.home_empty_button)

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText(buttonText).fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText(buttonText).performClick()

        composeTestRule.waitUntil(timeoutMillis = 5_000) { navigatedRecoverySetup }
        assertTrue(navigatedRecoverySetup)
    }
}

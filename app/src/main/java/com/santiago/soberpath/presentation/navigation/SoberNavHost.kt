package com.santiago.soberpath.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.santiago.soberpath.presentation.screen.dailycheckin.DailyCheckInScreen
import com.santiago.soberpath.presentation.screen.home.HomeScreen
import com.santiago.soberpath.presentation.screen.milestones.MilestonesScreen
import com.santiago.soberpath.presentation.screen.motivation.MotivationScreen
import com.santiago.soberpath.presentation.screen.onboarding.OnboardingScreen
import com.santiago.soberpath.presentation.screen.settings.SettingsScreen

@Composable
fun SoberNavHost(
    navController: NavHostController,
    startDestination: String = SoberDestination.Onboarding.route
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(SoberDestination.Onboarding.route) {
            OnboardingScreen(
                onCreateHabit = {
                    navController.navigate(SoberDestination.Home.route) {
                        popUpTo(SoberDestination.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        composable(SoberDestination.Home.route) {
            HomeScreen(
                onDailyCheckIn = { navController.navigate(SoberDestination.DailyCheckIn.route) },
                onMotivation = { navController.navigate(SoberDestination.Motivation.route) },
                onMilestones = { navController.navigate(SoberDestination.Milestones.route) },
                onSettings = { navController.navigate(SoberDestination.Settings.route) }
            )
        }
        composable(SoberDestination.DailyCheckIn.route) {
            DailyCheckInScreen(onBack = { navController.popBackStack() })
        }
        composable(SoberDestination.Motivation.route) {
            MotivationScreen(onBack = { navController.popBackStack() })
        }
        composable(SoberDestination.Milestones.route) {
            MilestonesScreen(onBack = { navController.popBackStack() })
        }
        composable(SoberDestination.Settings.route) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}


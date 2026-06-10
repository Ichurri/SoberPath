package com.santiago.soberpath.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.santiago.soberpath.presentation.screen.dailycheckin.DailyCheckInScreen
import com.santiago.soberpath.presentation.screen.home.HomeScreen
import com.santiago.soberpath.presentation.screen.milestones.MilestonesScreen
import com.santiago.soberpath.presentation.screen.motivation.MotivationScreen
import com.santiago.soberpath.presentation.screen.onboarding.OnboardingScreen
import com.santiago.soberpath.presentation.screen.recoverysetup.RecoverySetupScreen
import com.santiago.soberpath.presentation.screen.relapse.RelapseHistoryScreen
import com.santiago.soberpath.presentation.screen.relapse.RelapseScreen
import com.santiago.soberpath.presentation.screen.settings.SettingsScreen
import com.santiago.soberpath.presentation.screen.splash.SplashScreen
import org.koin.androidx.compose.koinViewModel
import com.santiago.soberpath.presentation.screen.habits.HabitListScreen

@Composable
fun SoberNavHost(
    navController: NavHostController,
    startDestination: String = SoberDestination.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(SoberDestination.Splash.route) {
            val viewModel: AppStartViewModel = koinViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()

            var splashFinished by remember { mutableStateOf(false) }

            LaunchedEffect(splashFinished, state.nextDestination) {
                val destination = state.nextDestination

                if (splashFinished && destination != null) {
                    navController.navigate(destination) {
                        popUpTo(SoberDestination.Splash.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            }

            SplashScreen(
                onAnimationFinished = {
                    splashFinished = true
                }
            )
        }

        composable(SoberDestination.Onboarding.route) {
            OnboardingScreen(
                onNavigateHome = {
                    navController.navigate(SoberDestination.RecoverySetup.route) {
                        popUpTo(SoberDestination.Onboarding.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(SoberDestination.RecoverySetup.route) {
            RecoverySetupScreen(
                onNavigateHome = {
                    navController.navigate(SoberDestination.Home.route) {
                        popUpTo(SoberDestination.RecoverySetup.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(SoberDestination.Home.route) {
            HomeScreen(
                onDailyCheckIn = {
                    navController.navigate(SoberDestination.DailyCheckIn.route)
                },
                onMotivation = {
                    navController.navigate(SoberDestination.Motivation.route)
                },
                onMilestones = {
                    navController.navigate(SoberDestination.Milestones.route)
                },
                onSettings = {
                    navController.navigate(SoberDestination.Settings.route)
                },
                onRecoverySetup = {
                    navController.navigate(SoberDestination.RecoverySetup.route)
                },
                onRegisterRelapse = {
                    navController.navigate(SoberDestination.RegisterRelapse.route)
                },
                onHabits = {
                    navController.navigate(SoberDestination.Habits.route)
                },
                onRelapseHistory = {
                    navController.navigate(SoberDestination.RelapseHistory.route)
                }
            )
        }

        composable(SoberDestination.DailyCheckIn.route) {
            DailyCheckInScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(SoberDestination.Motivation.route) {
            MotivationScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(SoberDestination.Milestones.route) {
            MilestonesScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(SoberDestination.Settings.route) {
            SettingsScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(SoberDestination.RegisterRelapse.route) {
            RelapseScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(SoberDestination.Habits.route) {
            HabitListScreen(
                onBack = {
                    navController.popBackStack()
                },
                onCreateNewHabit = {
                    navController.navigate(SoberDestination.RecoverySetup.route)
                }
            )
        }
        composable(SoberDestination.RelapseHistory.route) {
            RelapseHistoryScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
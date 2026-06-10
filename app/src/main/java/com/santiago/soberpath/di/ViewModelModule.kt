package com.santiago.soberpath.di

import com.santiago.soberpath.presentation.screen.dailycheckin.DailyCheckInViewModel
import com.santiago.soberpath.presentation.screen.home.HomeViewModel
import com.santiago.soberpath.presentation.screen.milestones.MilestonesViewModel
import com.santiago.soberpath.presentation.screen.motivation.MotivationViewModel
import com.santiago.soberpath.presentation.screen.onboarding.OnboardingViewModel
import com.santiago.soberpath.presentation.screen.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import com.santiago.soberpath.presentation.navigation.AppStartViewModel
import com.santiago.soberpath.presentation.screen.recoverysetup.RecoverySetupViewModel
import com.santiago.soberpath.presentation.screen.habits.HabitListViewModel
import com.santiago.soberpath.presentation.screen.relapsehistory.RelapseHistoryViewModel

object ViewModelModule {
    val module = module {
        viewModel { OnboardingViewModel(get(), get(), get()) }
        viewModel { HomeViewModel(get(), get(), get(), get(), get(), get()) }
        viewModel { DailyCheckInViewModel(get(), get()) }
        viewModel { MotivationViewModel(get(), get(), get()) }
        viewModel { MilestonesViewModel(get(), get()) }
        viewModel { SettingsViewModel(get(), get(), get()) }
        viewModel { AppStartViewModel(get(), get()) }
        viewModel { RecoverySetupViewModel (get()) }
        viewModel { HabitListViewModel(get(), get(), get()) }
        viewModel { RelapseHistoryViewModel(get(), get(), get()) }
    }
}

package com.santiago.soberpath.di

import com.santiago.soberpath.presentation.screen.dailycheckin.DailyCheckInViewModel
import com.santiago.soberpath.presentation.screen.home.HomeViewModel
import com.santiago.soberpath.presentation.screen.milestones.MilestonesViewModel
import com.santiago.soberpath.presentation.screen.motivation.MotivationViewModel
import com.santiago.soberpath.presentation.screen.onboarding.OnboardingViewModel
import com.santiago.soberpath.presentation.screen.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

object ViewModelModule {
    val module = module {
        viewModel { OnboardingViewModel(get()) }
        viewModel { HomeViewModel(get(), get(), get(), get(), get()) }
        viewModel { DailyCheckInViewModel(get(), get()) }
        viewModel { MotivationViewModel(get(), get(), get()) }
        viewModel { MilestonesViewModel(get(), get()) }
        viewModel { SettingsViewModel(get(), get()) }
    }
}

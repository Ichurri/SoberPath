package com.santiago.soberpath.di

import com.santiago.soberpath.data.repository.CheckInRepositoryImpl
import com.santiago.soberpath.data.repository.ConfigRepositoryImpl
import com.santiago.soberpath.data.repository.HabitRepositoryImpl
import com.santiago.soberpath.data.repository.MotivationRepositoryImpl
import com.santiago.soberpath.domain.repository.CheckInRepository
import com.santiago.soberpath.domain.repository.ConfigRepository
import com.santiago.soberpath.domain.repository.HabitRepository
import com.santiago.soberpath.domain.repository.MotivationRepository
import org.koin.dsl.module
import com.santiago.soberpath.data.repository.OnboardingRepositoryImpl
import com.santiago.soberpath.domain.repository.OnboardingRepository

object RepositoryModule {
    val module = module {
        single<HabitRepository> { HabitRepositoryImpl(get(), get()) }
        single<CheckInRepository> { CheckInRepositoryImpl(get()) }
        single<MotivationRepository> { MotivationRepositoryImpl(get()) }
        single<ConfigRepository> { ConfigRepositoryImpl(get()) }
        single<OnboardingRepository> { OnboardingRepositoryImpl(get()) }
    }
}

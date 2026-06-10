package com.santiago.soberpath.di

import com.santiago.soberpath.domain.usecase.AddMotivationReasonUseCase
import com.santiago.soberpath.domain.usecase.CompleteOnboardingUseCase
import com.santiago.soberpath.domain.usecase.CreateHabitUseCase
import com.santiago.soberpath.domain.usecase.GetActiveHabitUseCase
import com.santiago.soberpath.domain.usecase.GetDailyCheckInsUseCase
import com.santiago.soberpath.domain.usecase.GetMilestonesUseCase
import com.santiago.soberpath.domain.usecase.GetMotivationReasonsUseCase
import com.santiago.soberpath.domain.usecase.GetRelapsesUseCase
import com.santiago.soberpath.domain.usecase.GetRemoteConfigUseCase
import com.santiago.soberpath.domain.usecase.GetSobrietyProgressUseCase
import com.santiago.soberpath.domain.usecase.IsOnboardingCompletedUseCase
import com.santiago.soberpath.domain.usecase.RefreshRemoteConfigUseCase
import com.santiago.soberpath.domain.usecase.RegisterRelapseUseCase
import com.santiago.soberpath.domain.usecase.SaveDailyCheckInUseCase
import com.santiago.soberpath.domain.usecase.GetAllHabitsUseCase
import com.santiago.soberpath.domain.usecase.SetActiveHabitUseCase
import com.santiago.soberpath.domain.usecase.DeleteHabitUseCase
import org.koin.dsl.module

object UseCaseModule {
    val module = module {
        factory { CreateHabitUseCase(get()) }
        factory { GetActiveHabitUseCase(get()) }
        factory { RegisterRelapseUseCase(get(), get()) }
        factory { GetSobrietyProgressUseCase(get()) }
        factory { SaveDailyCheckInUseCase(get()) }
        factory { GetDailyCheckInsUseCase(get()) }
        factory { AddMotivationReasonUseCase(get()) }
        factory { GetMotivationReasonsUseCase(get()) }
        factory { GetMilestonesUseCase(get()) }
        factory { GetRelapsesUseCase(get()) }
        factory { GetRemoteConfigUseCase(get()) }
        factory { RefreshRemoteConfigUseCase(get()) }
        factory { IsOnboardingCompletedUseCase(get()) }
        factory { CompleteOnboardingUseCase(get()) }
        factory { GetAllHabitsUseCase(get()) }
        factory { SetActiveHabitUseCase(get()) }
        factory { DeleteHabitUseCase(get()) }
    }
}
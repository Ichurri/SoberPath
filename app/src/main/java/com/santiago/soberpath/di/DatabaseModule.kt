package com.santiago.soberpath.di

import com.santiago.soberpath.data.local.SoberPathDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

object DatabaseModule {
    val module = module {
        single { SoberPathDatabase.build(androidContext()) }
        single { get<SoberPathDatabase>().habitDao() }
        single { get<SoberPathDatabase>().dailyCheckInDao() }
        single { get<SoberPathDatabase>().motivationReasonDao() }
        single { get<SoberPathDatabase>().milestoneDao() }
    }
}


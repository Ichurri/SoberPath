package com.santiago.soberpath.di

import androidx.work.WorkManager
import com.santiago.soberpath.notification.NotificationHelper
import com.santiago.soberpath.notification.NotificationScheduler
import org.koin.dsl.module

object NotificationModule {
    val module = module {
        single { WorkManager.getInstance(get()) }
        single { NotificationHelper(get()) }
        single { NotificationScheduler(get(), get()) }
    }
}


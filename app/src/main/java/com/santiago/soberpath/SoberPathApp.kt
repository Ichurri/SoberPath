package com.santiago.soberpath

import android.app.Application
import com.santiago.soberpath.di.DatabaseModule
import com.santiago.soberpath.di.FirebaseModule
import com.santiago.soberpath.di.NotificationModule
import com.santiago.soberpath.di.RepositoryModule
import com.santiago.soberpath.di.UseCaseModule
import com.santiago.soberpath.di.ViewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SoberPathApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@SoberPathApp)
            modules(
                DatabaseModule.module,
                RepositoryModule.module,
                UseCaseModule.module,
                ViewModelModule.module,
                FirebaseModule.module,
                NotificationModule.module
            )
        }
    }
}


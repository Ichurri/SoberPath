package com.santiago.soberpath.notification

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.santiago.soberpath.R
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

class NotificationScheduler(
    private val context: Context,
    private val workManager: WorkManager
) {
    fun canPostNotifications(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun scheduleDailyReminder(hour: Int): ScheduleResult {
        if (!canPostNotifications()) return ScheduleResult.MissingPermission
        if (hour !in 0..23) return ScheduleResult.InvalidTime

        val delay = computeInitialDelay(hour)
        val request = PeriodicWorkRequestBuilder<DailyReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(delay.toMillis(), TimeUnit.MILLISECONDS)
            .build()

        workManager.enqueueUniquePeriodicWork(
            WORK_NAME_DAILY,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
        return ScheduleResult.Scheduled
    }

    fun cancelDailyReminder() {
        workManager.cancelUniqueWork(WORK_NAME_DAILY)
    }

    private fun computeInitialDelay(hour: Int): Duration {
        val now = LocalDateTime.now()
        val targetToday = LocalDateTime.of(now.toLocalDate(), LocalTime.of(hour, 0))
        val target = if (now.isAfter(targetToday)) {
            targetToday.plusDays(1)
        } else {
            targetToday
        }
        return Duration.between(now, target)
    }

    sealed interface ScheduleResult {
        object Scheduled : ScheduleResult
        object MissingPermission : ScheduleResult
        object InvalidTime : ScheduleResult
        data class Failed(val messageRes: Int) : ScheduleResult
    }

    companion object {
        const val WORK_NAME_DAILY = "daily_reminder_work"
    }
}


package com.santiago.soberpath.data.mapper

import com.santiago.soberpath.data.local.entity.HabitEntity
import com.santiago.soberpath.domain.model.Habit
import java.time.LocalDate
import java.time.LocalDateTime

private fun parseDateTimeSafely(dateStr: String): LocalDateTime {
    return try {
        if (dateStr.contains("T")) {
            LocalDateTime.parse(dateStr)
        } else {
            LocalDate.parse(dateStr).atStartOfDay()
        }
    } catch (e: Exception) {
        LocalDateTime.now()
    }
}

fun HabitEntity.toDomain(): Habit {
    return Habit(
        id = id,
        name = name,
        category = category,
        startDate = parseDateTimeSafely(startDate),
        lastRelapseDate = parseDateTimeSafely(lastRelapseDate),
        dailyCost = dailyCost,
        currency = currency,
        isActive = isActive
    )
}

fun Habit.toEntity(): HabitEntity {
    return HabitEntity(
        id = id,
        name = name,
        category = category,
        startDate = startDate.toString(),
        lastRelapseDate = lastRelapseDate.toString(),
        dailyCost = dailyCost,
        currency = currency,
        isActive = isActive
    )
}


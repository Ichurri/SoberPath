package com.santiago.soberpath.data.mapper

import com.santiago.soberpath.data.local.entity.HabitEntity
import com.santiago.soberpath.domain.model.Habit
import java.time.LocalDate

fun HabitEntity.toDomain(): Habit {
    return Habit(
        id = id,
        name = name,
        category = category,
        startDate = LocalDate.parse(startDate),
        lastRelapseDate = LocalDate.parse(lastRelapseDate),
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


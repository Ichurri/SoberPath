package com.santiago.soberpath.testutil

import com.santiago.soberpath.domain.model.Habit
import com.santiago.soberpath.domain.model.Relapse
import java.time.LocalDate
import java.time.LocalDateTime

/** Fábricas de modelos de dominio para tests, con valores por defecto razonables. */
fun testHabit(
    id: String = "habit-1",
    name: String = "Alcohol",
    category: String = "recovery",
    startDate: LocalDateTime = LocalDateTime.of(2026, 1, 1, 8, 0),
    lastRelapseDate: LocalDateTime = LocalDateTime.of(2026, 1, 1, 8, 0),
    dailyCost: Double = 20.0,
    currency: String = "Bs",
    isActive: Boolean = true
): Habit = Habit(
    id = id,
    name = name,
    category = category,
    startDate = startDate,
    lastRelapseDate = lastRelapseDate,
    dailyCost = dailyCost,
    currency = currency,
    isActive = isActive
)

fun testRelapse(
    id: String = "relapse-1",
    habitId: String = "habit-1",
    relapseDate: LocalDate = LocalDate.of(2026, 6, 1),
    cravingLevel: Int = 3,
    trigger: String = "Estrés",
    note: String = "",
    createdAt: LocalDateTime = LocalDateTime.of(2026, 6, 1, 12, 0)
): Relapse = Relapse(
    id = id,
    habitId = habitId,
    relapseDate = relapseDate,
    cravingLevel = cravingLevel,
    trigger = trigger,
    note = note,
    createdAt = createdAt
)

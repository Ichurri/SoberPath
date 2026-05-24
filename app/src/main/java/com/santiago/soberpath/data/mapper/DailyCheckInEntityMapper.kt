package com.santiago.soberpath.data.mapper

import com.santiago.soberpath.data.local.entity.DailyCheckInEntity
import com.santiago.soberpath.domain.model.DailyCheckIn
import java.time.LocalDate

fun DailyCheckInEntity.toDomain(): DailyCheckIn {
    return DailyCheckIn(
        id = id,
        habitId = habitId,
        date = LocalDate.parse(date),
        mood = mood,
        cravingLevel = cravingLevel,
        note = note,
        completedPledge = completedPledge
    )
}

fun DailyCheckIn.toEntity(): DailyCheckInEntity {
    return DailyCheckInEntity(
        id = id,
        habitId = habitId,
        date = date.toString(),
        mood = mood,
        cravingLevel = cravingLevel,
        note = note,
        completedPledge = completedPledge
    )
}


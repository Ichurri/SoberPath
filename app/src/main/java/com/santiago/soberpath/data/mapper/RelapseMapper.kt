package com.santiago.soberpath.data.mapper

import com.santiago.soberpath.data.local.entity.RelapseEntity
import com.santiago.soberpath.domain.model.Relapse
import java.time.LocalDate
import java.time.LocalDateTime

fun RelapseEntity.toDomain(): Relapse {
    return Relapse(
        id = id,
        habitId = habitId,
        relapseDate = LocalDate.parse(relapseDate),
        cravingLevel = cravingLevel,
        trigger = trigger,
        note = note,
        createdAt = LocalDateTime.parse(createdAt)
    )
}

fun Relapse.toEntity(): RelapseEntity {
    return RelapseEntity(
        id = id,
        habitId = habitId,
        relapseDate = relapseDate.toString(),
        cravingLevel = cravingLevel,
        trigger = trigger,
        note = note,
        createdAt = createdAt.toString()
    )
}
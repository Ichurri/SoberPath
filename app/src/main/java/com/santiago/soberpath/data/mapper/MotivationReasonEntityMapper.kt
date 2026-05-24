package com.santiago.soberpath.data.mapper

import com.santiago.soberpath.data.local.entity.MotivationReasonEntity
import com.santiago.soberpath.domain.model.MotivationReason
import java.time.LocalDateTime

fun MotivationReasonEntity.toDomain(): MotivationReason {
    return MotivationReason(
        id = id,
        habitId = habitId,
        text = text,
        createdAt = LocalDateTime.parse(createdAt)
    )
}

fun MotivationReason.toEntity(): MotivationReasonEntity {
    return MotivationReasonEntity(
        id = id,
        habitId = habitId,
        text = text,
        createdAt = createdAt.toString()
    )
}


package com.santiago.soberpath.data.mapper

import com.santiago.soberpath.data.local.entity.MilestoneEntity
import com.santiago.soberpath.domain.model.Milestone

fun MilestoneEntity.toDomain(): Milestone {
    return Milestone(
        id = id,
        title = title,
        daysRequired = daysRequired,
        achieved = achieved
    )
}

fun Milestone.toEntity(): MilestoneEntity {
    return MilestoneEntity(
        id = id,
        title = title,
        daysRequired = daysRequired,
        achieved = achieved
    )
}


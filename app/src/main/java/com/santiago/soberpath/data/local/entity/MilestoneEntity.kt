package com.santiago.soberpath.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "milestones")
data class MilestoneEntity(
    @PrimaryKey val id: String,
    val title: String,
    val daysRequired: Int,
    val achieved: Boolean
)


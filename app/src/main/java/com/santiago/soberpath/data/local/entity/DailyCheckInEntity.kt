package com.santiago.soberpath.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "daily_check_ins",
    indices = [Index(value = ["habitId"])])
data class DailyCheckInEntity(
    @PrimaryKey val id: String,
    val habitId: String,
    val date: String,
    val mood: String,
    val cravingLevel: Int,
    val note: String?,
    val completedPledge: Boolean
)


package com.santiago.soberpath.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "relapses",
    indices = [
        Index(value = ["habitId"])
    ]
)
data class RelapseEntity(
    @PrimaryKey val id: String,
    val habitId: String,
    val relapseDate: String,
    val cravingLevel: Int,
    val trigger: String,
    val note: String,
    val createdAt: String
)
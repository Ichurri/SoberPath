package com.santiago.soberpath.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "motivation_reasons",
    indices = [Index(value = ["habitId"])])
data class MotivationReasonEntity(
    @PrimaryKey val id: String,
    val habitId: String,
    val text: String,
    val createdAt: String
)


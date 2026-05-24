package com.santiago.soberpath.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val startDate: String,
    val lastRelapseDate: String,
    val dailyCost: Double,
    val currency: String,
    val isActive: Boolean
)


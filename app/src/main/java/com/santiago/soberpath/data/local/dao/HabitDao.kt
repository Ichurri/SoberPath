package com.santiago.soberpath.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.santiago.soberpath.data.local.entity.HabitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(habit: HabitEntity)

    @Query("SELECT * FROM habits WHERE isActive = 1 LIMIT 1")
    fun observeActiveHabit(): Flow<HabitEntity?>

    @Query("SELECT * FROM habits WHERE id = :habitId LIMIT 1")
    suspend fun getById(habitId: String): HabitEntity?

    @Query("SELECT * FROM habits WHERE id = :habitId LIMIT 1")
    fun observeById(habitId: String): Flow<HabitEntity?>
}


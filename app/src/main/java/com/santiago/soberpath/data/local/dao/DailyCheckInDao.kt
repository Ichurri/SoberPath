package com.santiago.soberpath.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.santiago.soberpath.data.local.entity.DailyCheckInEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyCheckInDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(checkIn: DailyCheckInEntity)

    @Query("SELECT * FROM daily_check_ins WHERE habitId = :habitId ORDER BY date DESC")
    fun observeByHabitId(habitId: String): Flow<List<DailyCheckInEntity>>
}


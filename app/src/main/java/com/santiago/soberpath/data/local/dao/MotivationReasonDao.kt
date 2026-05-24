package com.santiago.soberpath.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.santiago.soberpath.data.local.entity.MotivationReasonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MotivationReasonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reason: MotivationReasonEntity)

    @Query("SELECT * FROM motivation_reasons WHERE habitId = :habitId ORDER BY createdAt DESC")
    fun observeByHabitId(habitId: String): Flow<List<MotivationReasonEntity>>
}


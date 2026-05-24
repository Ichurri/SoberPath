package com.santiago.soberpath.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.santiago.soberpath.data.local.entity.MilestoneEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MilestoneDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(milestones: List<MilestoneEntity>)

    @Query("SELECT * FROM milestones ORDER BY daysRequired ASC")
    fun observeAll(): Flow<List<MilestoneEntity>>
}


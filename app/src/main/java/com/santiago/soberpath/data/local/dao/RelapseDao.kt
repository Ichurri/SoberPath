package com.santiago.soberpath.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.santiago.soberpath.data.local.entity.RelapseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RelapseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(relapse: RelapseEntity)

    @Query(
        """
        SELECT * FROM relapses 
        WHERE habitId = :habitId 
        ORDER BY relapseDate DESC, createdAt DESC
        """
    )
    fun observeByHabitId(habitId: String): Flow<List<RelapseEntity>>
}
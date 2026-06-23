package com.span.ashless.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgramDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: ProgramEntity)

    @Query("UPDATE programs SET isActive = 0 WHERE isActive = 1")
    suspend fun deactivateAll()

    @Query("SELECT * FROM programs WHERE isActive = 1 LIMIT 1")
    fun observeActiveProgram(): Flow<ProgramEntity?>
}

package com.span.ashless.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CigaretteEntryDao {
    @Insert
    suspend fun insert(entity: CigaretteEntryEntity)

    @Query("DELETE FROM cigarette_entries WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query(
        "SELECT * FROM cigarette_entries WHERE timestampMs >= :startMs AND timestampMs < :endMs " +
            "ORDER BY timestampMs DESC",
    )
    fun observeEntriesBetween(
        startMs: Long,
        endMs: Long,
    ): Flow<List<CigaretteEntryEntity>>

    @Query("SELECT * FROM cigarette_entries ORDER BY timestampMs DESC LIMIT 1")
    fun observeMostRecentEntry(): Flow<CigaretteEntryEntity?>
}

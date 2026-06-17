package com.span.ashless.data.datasource

import com.span.ashless.data.local.CigaretteEntryEntity
import kotlinx.coroutines.flow.Flow

interface EntryDataSource {
    suspend fun insert(entity: CigaretteEntryEntity)
    suspend fun deleteById(id: String)
    fun observeEntriesBetween(
        startMs: Long,
        endMs: Long,
    ): Flow<List<CigaretteEntryEntity>>
}

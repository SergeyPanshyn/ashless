package com.span.ashless.data.local

import com.span.ashless.data.datasource.EntryDataSource
import kotlinx.coroutines.flow.Flow

class RoomEntryDataSource(private val dao: CigaretteEntryDao) : EntryDataSource {
    override suspend fun insert(entity: CigaretteEntryEntity) = dao.insert(entity)

    override suspend fun deleteById(id: String) = dao.deleteById(id)

    override fun observeEntriesBetween(
        startMs: Long,
        endMs: Long,
    ): Flow<List<CigaretteEntryEntity>> = dao.observeEntriesBetween(startMs, endMs)
}

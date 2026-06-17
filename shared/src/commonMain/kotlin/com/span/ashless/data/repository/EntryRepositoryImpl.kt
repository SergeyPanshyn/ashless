package com.span.ashless.data.repository

import com.span.ashless.data.datasource.EntryDataSource
import com.span.ashless.data.local.CigaretteEntryEntity
import com.span.ashless.data.util.DayBoundaries
import com.span.ashless.domain.model.CigaretteEntry
import com.span.ashless.domain.repository.EntryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlin.uuid.Uuid

class EntryRepositoryImpl(private val dataSource: EntryDataSource) : EntryRepository {
    override suspend fun log(): CigaretteEntry {
        val timestampMs = Clock.System.now().toEpochMilliseconds()
        val entry = CigaretteEntry(id = Uuid.random().toString(), timestampMs = timestampMs)
        dataSource.insert(CigaretteEntryEntity(id = entry.id, timestampMs = entry.timestampMs))
        return entry
    }

    override suspend fun delete(id: String) = dataSource.deleteById(id)

    override fun observeTodayEntries(): Flow<List<CigaretteEntry>> {
        val (startMs, endMs) = DayBoundaries.todayRange()
        return dataSource.observeEntriesBetween(startMs, endMs)
            .map { entities -> entities.map { it.toDomain() } }
    }
}

private fun CigaretteEntryEntity.toDomain() = CigaretteEntry(id = id, timestampMs = timestampMs)

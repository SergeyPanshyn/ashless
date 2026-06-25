package com.span.ashless.data.repository

import com.span.ashless.data.datasource.EntryDataSource
import com.span.ashless.data.local.CigaretteEntryEntity
import com.span.ashless.data.util.DayBoundaries
import com.span.ashless.domain.model.CigaretteEntry
import com.span.ashless.domain.repository.EntryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import kotlin.time.Clock
import kotlin.uuid.Uuid

class EntryRepositoryImpl(private val dataSource: EntryDataSource) : EntryRepository {
    override suspend fun log(): CigaretteEntry {
        val id = Uuid.random()
        val smokedAt = Clock.System.now()
        dataSource.insert(CigaretteEntryEntity(id = id.toString(), timestampMs = smokedAt.toEpochMilliseconds()))
        return CigaretteEntry(id = id, smokedAt = smokedAt)
    }

    override suspend fun delete(id: Uuid) = dataSource.deleteById(id.toString())

    override fun observeTodayEntries(): Flow<List<CigaretteEntry>> {
        val (startMs, endMs) = DayBoundaries.todayRange()
        return dataSource.observeEntriesBetween(startMs, endMs)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun observeEntriesSince(from: Instant): Flow<List<CigaretteEntry>> =
        dataSource.observeEntriesBetween(from.toEpochMilliseconds(), Long.MAX_VALUE)
            .map { entities -> entities.map { it.toDomain() } }

    override fun observeMostRecentEntry(): Flow<CigaretteEntry?> =
        dataSource.observeMostRecentEntry().map { it?.toDomain() }
}

private fun CigaretteEntryEntity.toDomain() =
    CigaretteEntry(
        id = Uuid.parse(id),
        smokedAt = Instant.fromEpochMilliseconds(timestampMs),
    )

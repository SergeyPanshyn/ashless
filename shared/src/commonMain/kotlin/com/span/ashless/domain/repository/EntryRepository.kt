package com.span.ashless.domain.repository

import com.span.ashless.domain.model.CigaretteEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import kotlin.uuid.Uuid

interface EntryRepository {
    suspend fun log(): CigaretteEntry
    suspend fun delete(id: Uuid)
    fun observeTodayEntries(): Flow<List<CigaretteEntry>>
    fun observeEntriesSince(from: Instant): Flow<List<CigaretteEntry>>
    fun observeMostRecentEntry(): Flow<CigaretteEntry?>
}

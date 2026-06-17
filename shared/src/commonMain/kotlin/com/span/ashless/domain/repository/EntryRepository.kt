package com.span.ashless.domain.repository

import com.span.ashless.domain.model.CigaretteEntry
import kotlinx.coroutines.flow.Flow

interface EntryRepository {
    suspend fun log(): CigaretteEntry
    suspend fun delete(id: String)
    fun observeTodayEntries(): Flow<List<CigaretteEntry>>
}

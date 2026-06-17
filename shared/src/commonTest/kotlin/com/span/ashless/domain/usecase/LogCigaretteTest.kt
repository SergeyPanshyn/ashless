package com.span.ashless.domain.usecase

import com.span.ashless.domain.model.CigaretteEntry
import com.span.ashless.domain.repository.EntryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class LogCigaretteTest {
    private val fakeEntry = CigaretteEntry(id = "abc", timestampMs = 1_000L)
    private val repo = object : EntryRepository {
        var logCalled = 0

        override suspend fun log(): CigaretteEntry {
            logCalled++
            return fakeEntry
        }

        override suspend fun delete(id: String) = Unit

        override fun observeTodayEntries(): Flow<List<CigaretteEntry>> = flowOf(emptyList())
    }

    @Test
    fun invokeCallsRepositoryLogAndReturnsEntry() =
        runTest {
            val useCase = LogCigarette(repo)
            val result = useCase()
            assertEquals(1, repo.logCalled)
            assertNotNull(result)
            assertEquals(fakeEntry.id, result.id)
        }
}

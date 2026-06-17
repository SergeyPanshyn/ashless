package com.span.ashless.data.local

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CigaretteEntryDaoTest {
    private lateinit var db: AshlessDatabase
    private lateinit var dao: CigaretteEntryDao

    @BeforeTest
    fun setup() {
        db = Room.inMemoryDatabaseBuilder<AshlessDatabase>()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
        dao = db.entryDao()
    }

    @AfterTest
    fun teardown() {
        db.close()
    }

    @Test
    fun insertAndObserveEntry() =
        runTest {
            val entity = CigaretteEntryEntity(id = "1", timestampMs = 1_000L)
            dao.insert(entity)

            val result = dao.observeEntriesBetween(startMs = 0L, endMs = 2_000L).first()
            assertEquals(1, result.size)
            assertEquals("1", result.first().id)
        }

    @Test
    fun deleteByIdRemovesEntry() =
        runTest {
            dao.insert(CigaretteEntryEntity(id = "2", timestampMs = 500L))
            dao.deleteById("2")

            val result = dao.observeEntriesBetween(startMs = 0L, endMs = 2_000L).first()
            assertTrue(result.isEmpty())
        }

    @Test
    fun observeFiltersOutsideBounds() =
        runTest {
            dao.insert(CigaretteEntryEntity(id = "a", timestampMs = 100L))
            dao.insert(CigaretteEntryEntity(id = "b", timestampMs = 900L))

            val result = dao.observeEntriesBetween(startMs = 200L, endMs = 1_000L).first()
            assertEquals(1, result.size)
            assertEquals("b", result.first().id)
        }
}

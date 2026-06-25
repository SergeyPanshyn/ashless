package com.span.ashless.data.local

import com.span.ashless.data.datasource.ProgramDataSource
import kotlinx.coroutines.flow.Flow

class RoomProgramDataSource(private val dao: ProgramDao) : ProgramDataSource {
    override suspend fun upsert(entity: ProgramEntity) = dao.upsert(entity)

    override suspend fun deactivateAll() = dao.deactivateAll()

    override fun observeActiveProgram(): Flow<ProgramEntity?> = dao.observeActiveProgram()
}

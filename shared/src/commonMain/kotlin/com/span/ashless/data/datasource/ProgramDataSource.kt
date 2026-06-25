package com.span.ashless.data.datasource

import com.span.ashless.data.local.ProgramEntity
import kotlinx.coroutines.flow.Flow

interface ProgramDataSource {
    suspend fun upsert(entity: ProgramEntity)
    suspend fun deactivateAll()
    fun observeActiveProgram(): Flow<ProgramEntity?>
}

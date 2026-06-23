package com.span.ashless.domain.repository

import com.span.ashless.domain.model.Program
import kotlinx.coroutines.flow.Flow

interface ProgramRepository {
    suspend fun save(program: Program)
    suspend fun cancelActive()
    fun observeActiveProgram(): Flow<Program?>
}

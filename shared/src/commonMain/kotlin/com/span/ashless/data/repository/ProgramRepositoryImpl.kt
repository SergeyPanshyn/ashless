package com.span.ashless.data.repository

import com.span.ashless.data.datasource.ProgramDataSource
import com.span.ashless.data.local.ProgramEntity
import com.span.ashless.domain.model.Program
import com.span.ashless.domain.repository.ProgramRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlin.uuid.Uuid

class ProgramRepositoryImpl(private val dataSource: ProgramDataSource) : ProgramRepository {
    override suspend fun save(program: Program) {
        dataSource.deactivateAll()
        dataSource.upsert(program.toEntity())
    }

    override suspend fun cancelActive() = dataSource.deactivateAll()

    override fun observeActiveProgram(): Flow<Program?> = dataSource.observeActiveProgram().map { it?.toDomain() }
}

private fun Program.toEntity() =
    ProgramEntity(
        id = id.toString(),
        baselinePerDay = baselinePerDay,
        targetPerDay = targetPerDay,
        durationWeeks = durationWeeks,
        startDate = startDate.toString(),
        strategyId = strategyId,
        isActive = if (isActive) 1 else 0,
    )

private fun ProgramEntity.toDomain() =
    Program(
        id = Uuid.parse(id),
        baselinePerDay = baselinePerDay,
        targetPerDay = targetPerDay,
        durationWeeks = durationWeeks,
        startDate = LocalDate.parse(startDate),
        strategyId = strategyId,
        isActive = isActive == 1,
    )

package com.span.ashless.domain.usecase

import com.span.ashless.domain.model.ProgramProgress
import com.span.ashless.domain.reduction.ReductionStrategyRegistry
import com.span.ashless.domain.repository.ProgramRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlinx.datetime.until
import kotlin.time.Clock

class ObserveProgramProgress(
    private val programRepository: ProgramRepository,
    private val registry: ReductionStrategyRegistry,
) {
    operator fun invoke(): Flow<ProgramProgress?> =
        programRepository.observeActiveProgram().map { program ->
            if (program == null) return@map null
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            val strategy = registry.get(program.strategyId)
            val daysSinceStart = program.startDate.until(today, DateTimeUnit.DAY).toInt()
            val weekCurrent = ((daysSinceStart / 7) + 1).coerceIn(1, program.durationWeeks)
            val allowanceToday = strategy.allowanceForDay(program, today)
            val nextWeekDate = today.plus(7, DateTimeUnit.DAY)
            val nextWeekDays = program.startDate.until(nextWeekDate, DateTimeUnit.DAY).toInt()
            val nextWeekNumber = (nextWeekDays / 7) + 1
            val allowanceNextWeek = if (nextWeekNumber <= program.durationWeeks) {
                strategy.allowanceForDay(program, nextWeekDate)
            } else {
                null
            }
            val goalDate = program.startDate.plus(program.durationWeeks * 7, DateTimeUnit.DAY)
            ProgramProgress(
                weekCurrent = weekCurrent,
                weekTotal = program.durationWeeks,
                allowanceToday = allowanceToday,
                allowanceNextWeek = allowanceNextWeek,
                targetPerDay = program.targetPerDay,
                goalDate = goalDate,
            )
        }
}

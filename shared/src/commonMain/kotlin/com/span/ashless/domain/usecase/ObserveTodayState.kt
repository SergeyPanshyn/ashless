package com.span.ashless.domain.usecase

import com.span.ashless.domain.model.DayStatus
import com.span.ashless.domain.model.TodayState
import com.span.ashless.domain.reduction.ReductionStrategyRegistry
import com.span.ashless.domain.repository.EntryRepository
import com.span.ashless.domain.repository.ProgramRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlinx.datetime.until
import kotlin.time.Clock

class ObserveTodayState(
    private val entryRepository: EntryRepository,
    private val programRepository: ProgramRepository,
    private val registry: ReductionStrategyRegistry,
) {
    operator fun invoke(): Flow<TodayState> =
        combine(
            entryRepository.observeTodayEntries(),
            programRepository.observeActiveProgram(),
        ) { entries, program ->
            val count = entries.size
            if (program == null) {
                TodayState(count = count, status = DayStatus.NO_PROGRAM)
            } else {
                val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
                val strategy = registry.get(program.strategyId)
                val allowance = strategy.allowanceForDay(program, today)
                val nextWeekDate = today.plus(7, DateTimeUnit.DAY)
                val nextWeekDays = program.startDate.until(nextWeekDate, DateTimeUnit.DAY).toInt()
                val nextWeekNumber = (nextWeekDays / 7) + 1
                val allowanceNextWeek = if (nextWeekNumber <= program.durationWeeks) {
                    strategy.allowanceForDay(program, nextWeekDate)
                } else {
                    null
                }
                val remaining = (allowance - count).coerceAtLeast(0)
                val status = if (count > allowance) DayStatus.OVER_LIMIT else DayStatus.UNDER_LIMIT
                TodayState(
                    count = count,
                    allowance = allowance,
                    remaining = remaining,
                    allowanceNextWeek = allowanceNextWeek,
                    status = status,
                )
            }
        }
}

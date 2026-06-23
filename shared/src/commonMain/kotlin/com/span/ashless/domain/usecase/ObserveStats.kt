package com.span.ashless.domain.usecase

import com.span.ashless.domain.model.DayStats
import com.span.ashless.domain.model.StatsData
import com.span.ashless.domain.reduction.ReductionStrategyRegistry
import com.span.ashless.domain.repository.EntryRepository
import com.span.ashless.domain.repository.ProgramRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import kotlinx.datetime.until
import kotlin.time.Clock

private const val WINDOW_DAYS = 7

class ObserveStats(
    private val entryRepository: EntryRepository,
    private val programRepository: ProgramRepository,
    private val registry: ReductionStrategyRegistry,
) {
    operator fun invoke(): Flow<StatsData> =
        combine(
            entryRepository.observeEntriesSince(windowStart()),
            programRepository.observeActiveProgram(),
        ) { entries, program ->
            val tz = TimeZone.currentSystemDefault()
            val today = Clock.System.todayIn(tz)
            val days = (0 until WINDOW_DAYS).map { daysAgo ->
                val date = today.minus(daysAgo, DateTimeUnit.DAY)
                val count = entries.count { it.smokedAt.toLocalDateTime(tz).date == date }
                val allowance = program?.let {
                    registry.get(it.strategyId).allowanceForDay(it, date)
                }
                DayStats(date = date, count = count, allowance = allowance)
            }
            val weekCurrent = program?.let {
                val days = it.startDate.until(today, DateTimeUnit.DAY).toInt()
                ((days / 7) + 1).coerceIn(1, it.durationWeeks)
            }
            StatsData(
                days = days,
                programWeekCurrent = weekCurrent,
                programWeekTotal = program?.durationWeeks,
            )
        }

    private fun windowStart() =
        Clock.System.todayIn(TimeZone.currentSystemDefault())
            .minus(WINDOW_DAYS - 1, DateTimeUnit.DAY)
            .atStartOfDayIn(TimeZone.currentSystemDefault())
}

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
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import kotlinx.datetime.until
import kotlin.time.Clock
import kotlin.time.Instant

private const val WINDOW_DAYS = 7

class ObserveStats(
    private val entryRepository: EntryRepository,
    private val programRepository: ProgramRepository,
    private val registry: ReductionStrategyRegistry,
) {
    operator fun invoke(weekOffset: Int = 0): Flow<StatsData> {
        val tz = TimeZone.currentSystemDefault()
        val today = Clock.System.todayIn(tz)
        // Align to calendar week: Monday = ordinal 0 in kotlinx-datetime DayOfWeek
        val currentWeekMonday = today.minus(today.dayOfWeek.ordinal, DateTimeUnit.DAY)
        val windowStart = currentWeekMonday.minus(weekOffset * 7, DateTimeUnit.DAY)
        val windowEnd = windowStart.plus(WINDOW_DAYS - 1, DateTimeUnit.DAY)
        val windowStartInstant = windowStart.atStartOfDayIn(tz)
        val windowEndExclusive: Instant = windowEnd.plus(1, DateTimeUnit.DAY).atStartOfDayIn(tz)

        return combine(
            entryRepository.observeEntriesSince(windowStartInstant),
            programRepository.observeActiveProgram(),
        ) { allEntries, program ->
            val entries = allEntries.filter { it.smokedAt < windowEndExclusive }
            val days = (0 until WINDOW_DAYS).map { daysAgo ->
                val date = windowEnd.minus(daysAgo, DateTimeUnit.DAY)
                val count = entries.count { it.smokedAt.toLocalDateTime(tz).date == date }
                val allowance = program?.let {
                    registry.get(it.strategyId).allowanceForDay(it, date)
                }
                DayStats(date = date, count = count, allowance = allowance)
            }
            val weekCurrent = program?.let {
                val daysIn = it.startDate.until(today, DateTimeUnit.DAY).toInt()
                ((daysIn / 7) + 1).coerceIn(1, it.durationWeeks)
            }
            StatsData(
                days = days,
                programWeekCurrent = weekCurrent,
                programWeekTotal = program?.durationWeeks,
            )
        }
    }
}

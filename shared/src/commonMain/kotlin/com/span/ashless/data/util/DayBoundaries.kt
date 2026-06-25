package com.span.ashless.data.util

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

object DayBoundaries {
    fun todayRange(
        now: Instant = Clock.System.now(),
        tz: TimeZone = TimeZone.currentSystemDefault(),
    ): Pair<Long, Long> {
        val today = now.toLocalDateTime(tz).date
        val startMs = today.atStartOfDayIn(tz).toEpochMilliseconds()
        val endMs = today.plus(1, DateTimeUnit.DAY).atStartOfDayIn(tz).toEpochMilliseconds()
        return startMs to endMs
    }
}

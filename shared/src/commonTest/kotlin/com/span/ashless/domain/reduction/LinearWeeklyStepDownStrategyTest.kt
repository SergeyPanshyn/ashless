package com.span.ashless.domain.reduction

import com.span.ashless.domain.model.Program
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.Uuid

class LinearWeeklyStepDownStrategyTest {
    private val strategy = LinearWeeklyStepDownStrategy()
    private val startDate = LocalDate(2026, 1, 5)

    private fun program(
        baseline: Int = 20,
        target: Int = 0,
        weeks: Int = 8,
    ) = Program(
        id = Uuid.parse("00000000-0000-0000-0000-000000000001"),
        baselinePerDay = baseline,
        targetPerDay = target,
        durationWeeks = weeks,
        startDate = startDate,
        strategyId = strategy.id,
        isActive = true,
    )

    @Test
    fun week1ReturnsBaseline() {
        val p = program(baseline = 20, target = 0, weeks = 8)
        assertEquals(20, strategy.allowanceForDay(p, startDate))
    }

    @Test
    fun lastWeekReturnsTarget() {
        val p = program(baseline = 20, target = 0, weeks = 8)
        val lastWeekDay = startDate.plus(7 * 7, DateTimeUnit.DAY)
        assertEquals(0, strategy.allowanceForDay(p, lastWeekDay))
    }

    @Test
    fun middleWeekIsInterpolated() {
        val p = program(baseline = 14, target = 0, weeks = 8)
        // week 4 (0-indexed day = 21): b - (b-t) * (4-1)/(8-1) = 14 - 14 * 3/7 = 14 - 6 = 8
        val week4Day = startDate.plus(21, DateTimeUnit.DAY)
        assertEquals(8, strategy.allowanceForDay(p, week4Day))
    }

    @Test
    fun beforeProgramStartClampsToWeek1() {
        val p = program(baseline = 20, target = 0, weeks = 8)
        val beforeStart = LocalDate(2025, 12, 1)
        assertEquals(20, strategy.allowanceForDay(p, beforeStart))
    }

    @Test
    fun afterProgramEndClampsToLastWeek() {
        val p = program(baseline = 20, target = 0, weeks = 8)
        val wayAfter = startDate.plus(200, DateTimeUnit.DAY)
        assertEquals(0, strategy.allowanceForDay(p, wayAfter))
    }

    @Test
    fun cutDownTargetNonZero() {
        val p = program(baseline = 20, target = 10, weeks = 6)
        // week 1: 20, week 6: 10
        assertEquals(20, strategy.allowanceForDay(p, startDate))
        val week6Day = startDate.plus(35, DateTimeUnit.DAY)
        assertEquals(10, strategy.allowanceForDay(p, week6Day))
    }

    @Test
    fun sameDayDifferentWeeksAreEqual() {
        val p = program(baseline = 20, target = 0, weeks = 8)
        // Mon and Sun of week 1 should both be week 1
        val monday = startDate
        val sunday = startDate.plus(6, DateTimeUnit.DAY)
        assertEquals(
            strategy.allowanceForDay(p, monday),
            strategy.allowanceForDay(p, sunday),
        )
    }
}

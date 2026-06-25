package com.span.ashless.domain.reduction

import com.span.ashless.domain.model.Program
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.until
import kotlin.math.roundToInt

class LinearWeeklyStepDownStrategy : ReductionStrategy {
    override val id = "linear_weekly_step_down"

    override fun allowanceForDay(
        program: Program,
        date: LocalDate,
    ): Int {
        val daysSinceStart = program.startDate.until(date, DateTimeUnit.DAY).toInt()
        val weekNum = ((daysSinceStart / 7) + 1).coerceIn(1, program.durationWeeks)
        val baseline = program.baselinePerDay.toDouble()
        val target = program.targetPerDay.toDouble()
        val totalWeeks = program.durationWeeks.toDouble()
        return if (totalWeeks <= 1) {
            target.roundToInt()
        } else {
            (baseline - (baseline - target) * (weekNum - 1) / (totalWeeks - 1)).roundToInt()
        }
    }
}

package com.span.ashless.domain.model

import kotlinx.datetime.LocalDate

data class DayStats(
    val date: LocalDate,
    val count: Int,
    val allowance: Int?,
)

data class StatsData(
    val days: List<DayStats>,
    val programWeekCurrent: Int?,
    val programWeekTotal: Int?,
)

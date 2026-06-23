package com.span.ashless.domain.model

data class TodayState(
    val count: Int = 0,
    val allowance: Int? = null,
    val remaining: Int? = null,
    val allowanceNextWeek: Int? = null,
    val status: DayStatus = DayStatus.NO_PROGRAM,
)

package com.span.ashless.domain.model

import kotlinx.datetime.LocalDate

data class ProgramProgress(
    val weekCurrent: Int,
    val weekTotal: Int,
    val allowanceToday: Int,
    val allowanceNextWeek: Int?,
    val targetPerDay: Int,
    val goalDate: LocalDate,
)

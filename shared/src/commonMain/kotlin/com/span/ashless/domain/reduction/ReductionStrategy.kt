package com.span.ashless.domain.reduction

import com.span.ashless.domain.model.Program
import kotlinx.datetime.LocalDate

interface ReductionStrategy {
    val id: String
    fun allowanceForDay(
        program: Program,
        date: LocalDate,
    ): Int
}

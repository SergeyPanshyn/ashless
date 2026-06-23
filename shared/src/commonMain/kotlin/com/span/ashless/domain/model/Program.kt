package com.span.ashless.domain.model

import kotlinx.datetime.LocalDate
import kotlin.uuid.Uuid

data class Program(
    val id: Uuid,
    val baselinePerDay: Int,
    val targetPerDay: Int,
    val durationWeeks: Int,
    val startDate: LocalDate,
    val strategyId: String,
    val isActive: Boolean,
)

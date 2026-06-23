package com.span.ashless.presentation.program

data class WeekPreview(
    val week: Int,
    val allowance: Int,
)

data class ProgramSetupUiState(
    val baselinePerDay: Int = 20,
    val isQuitGoal: Boolean = true,
    val cutToPerDay: Int = 5,
    val durationWeeks: Int = 8,
    val weeklyPreview: List<WeekPreview> = emptyList(),
    val canStart: Boolean = true,
)

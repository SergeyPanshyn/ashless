package com.span.ashless.presentation.program

data class ProgramProgressUiState(
    val weekLabel: String = "",
    val allowanceLabel: String = "",
    val nextWeekLabel: String = "",
    val goalLabel: String = "",
    val goalDateLabel: String = "",
    val showCancelConfirm: Boolean = false,
)

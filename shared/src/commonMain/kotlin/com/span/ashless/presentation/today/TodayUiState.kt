package com.span.ashless.presentation.today

enum class TodayStatusStyle { ON_TRACK, OVER_LIMIT, NO_PROGRAM }

sealed interface LogButtonState {
    data object Idle : LogButtonState
    data class Logged(val timeLabel: String) : LogButtonState
}

data class TodayUiState(
    val remainingCount: Int = 0,
    val countLabel: String = "today",
    val ringProgress: Float = 0f,
    val statusStyle: TodayStatusStyle = TodayStatusStyle.NO_PROGRAM,
    val statusLabel: String = "",
    val footerText: String = "",
    val timerText: String = "",
    val buttonState: LogButtonState = LogButtonState.Idle,
)

package com.span.ashless.presentation.today

enum class TodayStatusStyle { ON_TRACK, OVER_LIMIT }

sealed interface LogButtonState {
    data object Idle : LogButtonState
    data class Logged(val timeLabel: String) : LogButtonState
}

data class TodayUiState(
    val remainingCount: Int = 20,
    val ringProgress: Float = 0f,
    val statusStyle: TodayStatusStyle = TodayStatusStyle.ON_TRACK,
    val statusLabel: String = "On track",
    val buttonState: LogButtonState = LogButtonState.Idle,
)

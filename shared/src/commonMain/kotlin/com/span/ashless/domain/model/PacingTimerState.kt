package com.span.ashless.domain.model

sealed class PacingTimerState {
    data class Countdown(val minutesRemaining: Int) : PacingTimerState()
    data class WindowOpen(val minutesSinceLast: Int) : PacingTimerState()
    data class ElapsedAwareness(val minutesSinceLast: Int) : PacingTimerState()
    object Hidden : PacingTimerState()
}

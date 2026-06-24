package com.span.ashless.domain.model

private const val MIN_GAP_HOURS = 4
private const val MINUTES_PER_HOUR = 60

data class WakingHours(val wakeHour: Int = 7, val bedHour: Int = 23) {
    val wakingMinutes: Int get() = (bedHour - wakeHour) * MINUTES_PER_HOUR

    companion object {
        val DEFAULT = WakingHours()
        const val MIN_WAKE_HOUR = 0
        const val MAX_BED_HOUR = 23
        const val MIN_GAP = MIN_GAP_HOURS
    }
}

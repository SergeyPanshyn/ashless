package com.span.ashless.presentation.stats

sealed class StatsIntent {
    object PreviousWeek : StatsIntent()
    object NextWeek : StatsIntent()
}

package com.span.ashless.presentation.today

sealed interface TodayIntent {
    data object Log : TodayIntent
    data object Undo : TodayIntent
}

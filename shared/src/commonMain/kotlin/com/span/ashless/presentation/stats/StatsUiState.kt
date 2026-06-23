package com.span.ashless.presentation.stats

data class BarData(
    val label: String,
    val count: Int,
    val allowance: Int?,
    val isToday: Boolean,
    val isOverLimit: Boolean,
)

data class SummaryCard(
    val title: String,
    val value: String,
)

data class StatsUiState(
    val bars: List<BarData> = emptyList(),
    val summaryCards: List<SummaryCard> = emptyList(),
    val programLabel: String = "",
)

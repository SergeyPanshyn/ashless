package com.span.ashless.presentation.history

import kotlin.uuid.Uuid

data class HistoryEntry(
    val id: Uuid,
    val timeLabel: String,
)

data class HistoryDayGroup(
    val dayLabel: String,
    val totalLabel: String,
    val entries: List<HistoryEntry>,
)

data class HistoryUiState(
    val groups: List<HistoryDayGroup> = emptyList(),
    val isEmpty: Boolean = true,
)

package com.span.ashless.presentation.history

import kotlin.uuid.Uuid

sealed interface HistoryIntent {
    data class DeleteEntry(val id: Uuid) : HistoryIntent
}

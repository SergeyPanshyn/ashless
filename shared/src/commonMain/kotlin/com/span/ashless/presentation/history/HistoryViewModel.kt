package com.span.ashless.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.span.ashless.domain.model.CigaretteEntry
import com.span.ashless.domain.usecase.DeleteEntry
import com.span.ashless.domain.usecase.ObserveHistory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import kotlin.time.Clock

private val MONTH_NAMES = listOf(
    "Jan", "Feb", "Mar", "Apr", "May", "Jun",
    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec",
)
private val DAY_NAMES = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

class HistoryViewModel(
    private val observeHistory: ObserveHistory,
    private val deleteEntryUseCase: DeleteEntry,
) : ViewModel() {
    private val _state = MutableStateFlow(HistoryUiState())
    val state: StateFlow<HistoryUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            observeHistory().collect { entries ->
                _state.value = buildUiState(entries)
            }
        }
    }

    fun onIntent(intent: HistoryIntent) {
        when (intent) {
            is HistoryIntent.DeleteEntry -> viewModelScope.launch { deleteEntryUseCase(intent.id) }
        }
    }

    private fun buildUiState(entries: List<CigaretteEntry>): HistoryUiState {
        if (entries.isEmpty()) return HistoryUiState(isEmpty = true)
        val tz = TimeZone.currentSystemDefault()
        val today = Clock.System.todayIn(tz)
        val yesterday = today.minus(1, DateTimeUnit.DAY)
        val groups = entries
            .groupBy { it.smokedAt.toLocalDateTime(tz).date }
            .entries
            .sortedByDescending { it.key }
            .map { (date, dayEntries) ->
                val label = when (date) {
                    today -> "Today"
                    yesterday -> "Yesterday"
                    else -> buildDateLabel(date.dayOfWeek.ordinal, date.monthNumber, date.dayOfMonth)
                }
                val count = dayEntries.size
                HistoryDayGroup(
                    dayLabel = label,
                    totalLabel = "$count ${if (count == 1) "cigarette" else "cigarettes"}",
                    entries = dayEntries.sortedByDescending { it.smokedAt }.map { entry ->
                        val ldt = entry.smokedAt.toLocalDateTime(tz)
                        HistoryEntry(
                            id = entry.id,
                            timeLabel = formatTime(ldt.hour, ldt.minute),
                        )
                    },
                )
            }
        return HistoryUiState(groups = groups, isEmpty = false)
    }

    private fun buildDateLabel(
        dayOfWeekOrdinal: Int,
        monthNumber: Int,
        dayOfMonth: Int,
    ): String = "${DAY_NAMES[dayOfWeekOrdinal]}, ${MONTH_NAMES[monthNumber - 1]} $dayOfMonth"

    private fun formatTime(
        hour: Int,
        minute: Int,
    ): String = "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
}

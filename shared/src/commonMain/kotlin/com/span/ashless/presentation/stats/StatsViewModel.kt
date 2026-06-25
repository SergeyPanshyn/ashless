package com.span.ashless.presentation.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.span.ashless.domain.model.DayStats
import com.span.ashless.domain.model.StatsData
import com.span.ashless.domain.usecase.ObserveStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.math.roundToInt
import kotlin.time.Clock

class StatsViewModel(private val observeStats: ObserveStats) : ViewModel() {
    private val _state = MutableStateFlow(StatsUiState())
    val state: StateFlow<StatsUiState> = _state.asStateFlow()

    private val weekOffset = MutableStateFlow(0)

    init {
        viewModelScope.launch {
            weekOffset
                .flatMapLatest { offset -> observeStats(offset) }
                .collect { data -> _state.value = buildUiState(data, weekOffset.value) }
        }
    }

    fun onIntent(intent: StatsIntent) {
        when (intent) {
            StatsIntent.PreviousWeek -> weekOffset.update { it + 1 }
            StatsIntent.NextWeek -> weekOffset.update { (it - 1).coerceAtLeast(0) }
        }
    }

    private fun buildUiState(
        data: StatsData,
        offset: Int,
    ): StatsUiState {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val bars = data.days.reversed().map { day ->
            day.toBarData(isToday = day.date == today)
        }
        val weekTotal = data.days.sumOf { it.count }
        val avgTenths = (data.days.map { it.count }.average() * 10).roundToInt()
        val avgLabel = "${avgTenths / 10}.${avgTenths % 10}"
        val todayCount = data.days.firstOrNull()?.count ?: 0
        return StatsUiState(
            bars = bars,
            summaryCards = listOf(
                SummaryCard("Today", todayCount.toString()),
                SummaryCard("7-day avg", avgLabel),
                SummaryCard("This week", weekTotal.toString()),
            ),
            programLabel = buildProgramLabel(data),
            weekLabel = buildWeekLabel(data, offset),
            canGoNext = offset > 0,
        )
    }

    private fun buildWeekLabel(
        data: StatsData,
        offset: Int,
    ): String {
        if (offset == 0) return "Last 7 days"
        val endDate = data.days.firstOrNull()?.date ?: return "Last 7 days"
        val startDate = data.days.lastOrNull()?.date ?: return "Last 7 days"
        return "${startDate.toDisplayDate()}–${endDate.toDisplayDate()}"
    }

    private fun buildProgramLabel(data: StatsData): String {
        val w = data.programWeekCurrent ?: return ""
        val total = data.programWeekTotal ?: return ""
        return "Week $w of $total"
    }
}

private fun LocalDate.toDisplayDate(): String {
    val d = dayOfMonth.toString().padStart(2, '0')
    val m = monthNumber.toString().padStart(2, '0')
    return "$d.$m"
}

private fun DayStats.toBarData(isToday: Boolean): BarData {
    val over = allowance != null && count > allowance
    return BarData(
        label = date.toDisplayDate(),
        count = count,
        allowance = allowance,
        isToday = isToday,
        isOverLimit = over,
    )
}

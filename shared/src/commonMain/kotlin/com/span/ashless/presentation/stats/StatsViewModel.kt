package com.span.ashless.presentation.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.span.ashless.domain.model.DayStats
import com.span.ashless.domain.model.StatsData
import com.span.ashless.domain.usecase.ObserveStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private val DAY_NAMES = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

class StatsViewModel(private val observeStats: ObserveStats) : ViewModel() {
    private val _state = MutableStateFlow(StatsUiState())
    val state: StateFlow<StatsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            observeStats().collect { data -> _state.value = buildUiState(data) }
        }
    }

    private fun buildUiState(data: StatsData): StatsUiState {
        val bars = data.days.reversed().mapIndexed { index, day ->
            day.toBarData(isToday = index == data.days.size - 1)
        }
        val weekTotal = data.days.sumOf { it.count }
        val avgTenths = (data.days.map { it.count }.average() * 10).roundToInt()
        val avgLabel = "${avgTenths / 10}.${avgTenths % 10}"
        val todayCount = data.days.firstOrNull()?.count ?: 0
        val programLabel = buildProgramLabel(data)
        return StatsUiState(
            bars = bars,
            summaryCards = listOf(
                SummaryCard("Today", todayCount.toString()),
                SummaryCard("7-day avg", avgLabel),
                SummaryCard("This week", weekTotal.toString()),
            ),
            programLabel = programLabel,
        )
    }

    private fun buildProgramLabel(data: StatsData): String {
        val w = data.programWeekCurrent ?: return ""
        val total = data.programWeekTotal ?: return ""
        return "Week $w of $total"
    }
}

private fun DayStats.toBarData(isToday: Boolean): BarData {
    val over = allowance != null && count > allowance
    return BarData(
        label = if (isToday) "Today" else DAY_NAMES[date.dayOfWeek.ordinal],
        count = count,
        allowance = allowance,
        isToday = isToday,
        isOverLimit = over,
    )
}

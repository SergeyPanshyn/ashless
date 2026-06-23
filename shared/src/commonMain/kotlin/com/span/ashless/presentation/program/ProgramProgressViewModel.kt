package com.span.ashless.presentation.program

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.span.ashless.domain.usecase.ObserveProgramProgress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProgramProgressViewModel(observeProgramProgress: ObserveProgramProgress) : ViewModel() {
    private val _state = MutableStateFlow(ProgramProgressUiState())
    val state: StateFlow<ProgramProgressUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            observeProgramProgress().collect { progress ->
                if (progress == null) return@collect
                _state.value = ProgramProgressUiState(
                    weekLabel = "Week ${progress.weekCurrent} of ${progress.weekTotal}",
                    allowanceLabel = "${progress.allowanceToday} per day this week",
                    nextWeekLabel = progress.allowanceNextWeek?.let { "Next week: $it/day" } ?: "",
                    goalLabel = if (progress.targetPerDay == 0) "Goal: Quit" else "Goal: ${progress.targetPerDay}/day",
                    goalDateLabel = "Target: ${progress.goalDate}",
                )
            }
        }
    }
}

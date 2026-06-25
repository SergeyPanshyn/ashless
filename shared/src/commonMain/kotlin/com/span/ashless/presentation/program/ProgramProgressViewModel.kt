package com.span.ashless.presentation.program

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.span.ashless.domain.usecase.CancelProgram
import com.span.ashless.domain.usecase.ObserveProgramProgress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProgramProgressViewModel(
    observeProgramProgress: ObserveProgramProgress,
    private val cancelProgram: CancelProgram,
) : ViewModel() {
    private val _state = MutableStateFlow(ProgramProgressUiState())
    val state: StateFlow<ProgramProgressUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            observeProgramProgress().collect { progress ->
                if (progress == null) {
                    _state.value = ProgramProgressUiState()
                    return@collect
                }
                _state.update { current ->
                    current.copy(
                        weekLabel = "Week ${progress.weekCurrent} of ${progress.weekTotal}",
                        allowanceLabel = "${progress.allowanceToday} per day this week",
                        nextWeekLabel = progress.allowanceNextWeek?.let { "Next week: $it/day" } ?: "",
                        goalLabel = if (progress.targetPerDay == 0) {
                            "Goal: Quit"
                        } else {
                            "Goal: ${progress.targetPerDay}/day"
                        },
                        goalDateLabel = "Target: ${progress.goalDate}",
                    )
                }
            }
        }
    }

    fun onIntent(intent: ProgramProgressIntent) {
        when (intent) {
            ProgramProgressIntent.CancelTapped ->
                _state.update { it.copy(showCancelConfirm = true) }
            ProgramProgressIntent.CancelDismissed ->
                _state.update { it.copy(showCancelConfirm = false) }
            ProgramProgressIntent.CancelConfirmed -> {
                _state.update { it.copy(showCancelConfirm = false) }
                viewModelScope.launch { cancelProgram() }
            }
        }
    }
}

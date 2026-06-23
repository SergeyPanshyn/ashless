package com.span.ashless.presentation.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.span.ashless.domain.model.DayStatus
import com.span.ashless.domain.model.TodayState
import com.span.ashless.domain.usecase.DeleteEntry
import com.span.ashless.domain.usecase.LogCigarette
import com.span.ashless.domain.usecase.ObserveTodayState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.uuid.Uuid

private const val UNDO_TIMEOUT_MS = 5_000L

class TodayViewModel(
    observeTodayState: ObserveTodayState,
    private val logCigarette: LogCigarette,
    private val deleteEntry: DeleteEntry,
) : ViewModel() {
    private var todayState = TodayState()
    private var lastLoggedId: Uuid? = null
    private var undoTimerJob: Job? = null

    private val _state = MutableStateFlow(buildUiState())
    val state: StateFlow<TodayUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            observeTodayState().collect { state ->
                todayState = state
                refreshState()
            }
        }
    }

    fun onIntent(intent: TodayIntent) {
        when (intent) {
            TodayIntent.Log -> log()
            TodayIntent.Undo -> undo()
        }
    }

    private fun log() {
        viewModelScope.launch {
            val entry = logCigarette()
            lastLoggedId = entry.id
            val ldt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            val timeLabel =
                "${ldt.hour.toString().padStart(2, '0')}:${ldt.minute.toString().padStart(2, '0')}"
            refreshState(buttonState = LogButtonState.Logged(timeLabel))
            scheduleUndoTimeout()
        }
    }

    private fun undo() {
        viewModelScope.launch {
            val id = lastLoggedId ?: return@launch
            undoTimerJob?.cancel()
            undoTimerJob = null
            deleteEntry(id)
            clearUndo()
        }
    }

    private fun scheduleUndoTimeout() {
        undoTimerJob?.cancel()
        undoTimerJob = viewModelScope.launch {
            delay(UNDO_TIMEOUT_MS)
            clearUndo()
        }
    }

    private fun clearUndo() {
        lastLoggedId = null
        refreshState(buttonState = LogButtonState.Idle)
    }

    private fun refreshState(buttonState: LogButtonState = _state.value.buttonState) {
        _state.value = buildUiState(buttonState)
    }

    private fun buildUiState(buttonState: LogButtonState = LogButtonState.Idle): TodayUiState {
        return when (todayState.status) {
            DayStatus.NO_PROGRAM -> TodayUiState(
                remainingCount = todayState.count,
                ringProgress = 0f,
                statusStyle = TodayStatusStyle.NO_PROGRAM,
                statusLabel = "Set up your program",
                footerText = "",
                buttonState = buttonState,
            )
            DayStatus.UNDER_LIMIT -> {
                val allowance = todayState.allowance ?: 0
                TodayUiState(
                    remainingCount = todayState.remaining ?: 0,
                    ringProgress = if (allowance > 0) {
                        (todayState.count.toFloat() / allowance).coerceIn(0f, 1f)
                    } else {
                        0f
                    },
                    statusStyle = TodayStatusStyle.ON_TRACK,
                    statusLabel = "On track",
                    footerText = buildFooterText(),
                    buttonState = buttonState,
                )
            }
            DayStatus.OVER_LIMIT -> {
                val allowance = todayState.allowance ?: 0
                TodayUiState(
                    remainingCount = 0,
                    ringProgress = 1f,
                    statusStyle = TodayStatusStyle.OVER_LIMIT,
                    statusLabel = "${todayState.count - allowance} over today",
                    footerText = buildFooterText(),
                    buttonState = buttonState,
                )
            }
        }
    }

    private fun buildFooterText(): String {
        val allowance = todayState.allowance ?: return ""
        val next = todayState.allowanceNextWeek
        return if (next != null) {
            "This week: $allowance/day · next week $next"
        } else {
            "This week: $allowance/day"
        }
    }
}

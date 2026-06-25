package com.span.ashless.presentation.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.span.ashless.domain.model.DayStatus
import com.span.ashless.domain.model.PacingTimerState
import com.span.ashless.domain.model.TodayState
import com.span.ashless.domain.usecase.DeleteEntry
import com.span.ashless.domain.usecase.LogCigarette
import com.span.ashless.domain.usecase.ObservePacingTimer
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
    observePacingTimer: ObservePacingTimer,
) : ViewModel() {
    private var todayState = TodayState()
    private var pacingTimerState: PacingTimerState = PacingTimerState.Hidden
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
        viewModelScope.launch {
            observePacingTimer().collect { timerState ->
                pacingTimerState = timerState
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
                countLabel = "today",
                ringProgress = 0f,
                statusStyle = TodayStatusStyle.NO_PROGRAM,
                statusLabel = "Set up your program",
                footerText = "",
                timerText = formatTimer(pacingTimerState),
                buttonState = buttonState,
            )
            DayStatus.UNDER_LIMIT -> {
                val allowance = todayState.allowance ?: 0
                TodayUiState(
                    remainingCount = todayState.remaining ?: 0,
                    countLabel = "left",
                    ringProgress = if (allowance > 0) {
                        (todayState.count.toFloat() / allowance).coerceIn(0f, 1f)
                    } else {
                        0f
                    },
                    statusStyle = TodayStatusStyle.ON_TRACK,
                    statusLabel = "On track",
                    footerText = buildFooterText(),
                    timerText = formatTimer(pacingTimerState),
                    buttonState = buttonState,
                )
            }
            DayStatus.OVER_LIMIT -> {
                val allowance = todayState.allowance ?: 0
                TodayUiState(
                    remainingCount = 0,
                    countLabel = "left",
                    ringProgress = 1f,
                    statusStyle = TodayStatusStyle.OVER_LIMIT,
                    statusLabel = "${todayState.count - allowance} over today",
                    footerText = buildFooterText(),
                    timerText = "",
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

    private fun formatTimer(state: PacingTimerState): String =
        when (state) {
            is PacingTimerState.Countdown -> {
                val h = state.minutesRemaining / 60
                val m = state.minutesRemaining % 60
                if (h > 0) "Next in: ${h}h ${m}m" else "Next in: ${m}m"
            }
            is PacingTimerState.WindowOpen -> {
                val h = state.minutesSinceLast / 60
                val m = state.minutesSinceLast % 60
                val elapsed = if (h > 0) "${h}h ${m}m ago" else "${m}m ago"
                "Window open · $elapsed"
            }
            is PacingTimerState.ElapsedAwareness -> {
                val h = state.minutesSinceLast / 60
                val m = state.minutesSinceLast % 60
                if (h > 0) "Last: ${h}h ${m}m ago" else "Last: ${m}m ago"
            }
            PacingTimerState.Hidden -> ""
        }
}

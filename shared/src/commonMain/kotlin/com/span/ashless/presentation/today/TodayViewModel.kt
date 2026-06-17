package com.span.ashless.presentation.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.span.ashless.domain.usecase.DeleteEntry
import com.span.ashless.domain.usecase.LogCigarette
import com.span.ashless.domain.usecase.ObserveTodayState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.milliseconds

private const val UNDO_TIMEOUT_MS = 5_000L

class TodayViewModel(
    observeTodayState: ObserveTodayState,
    private val logCigarette: LogCigarette,
    private val deleteEntry: DeleteEntry,
    private val clock: Clock = Clock.System,
) : ViewModel() {

    private var count = 0
    private val limit = 10
    private var lastLoggedId: String? = null
    private var undoTimerJob: Job? = null

    private val _state = MutableStateFlow(buildUiState())
    val state: StateFlow<TodayUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            observeTodayState().collect { todayState ->
                count = todayState.count
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
            val ldt = clock.now().toLocalDateTime(TimeZone.currentSystemDefault())
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
            delay(UNDO_TIMEOUT_MS.milliseconds)
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
        val isOver = count >= limit
        return TodayUiState(
            remainingCount = (limit - count).coerceAtLeast(0),
            ringProgress = (count.toFloat() / limit).coerceIn(0f, 1f),
            statusStyle = if (isOver) TodayStatusStyle.OVER_LIMIT else TodayStatusStyle.ON_TRACK,
            statusLabel = if (isOver) "${count - limit} over today" else "On track",
            buttonState = buttonState,
        )
    }
}

package com.span.ashless.presentation.program

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.span.ashless.domain.model.Program
import com.span.ashless.domain.reduction.LinearWeeklyStepDownStrategy
import com.span.ashless.domain.usecase.CreateProgram
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.uuid.Uuid

private val strategy = LinearWeeklyStepDownStrategy()

class ProgramSetupViewModel(private val createProgram: CreateProgram) : ViewModel() {
    private val _state = MutableStateFlow(ProgramSetupUiState())
    val state: StateFlow<ProgramSetupUiState> = _state.asStateFlow()

    private val _effects = Channel<ProgramSetupEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        _state.value = rebuildState(_state.value)
    }

    fun onIntent(intent: ProgramSetupIntent) {
        val current = _state.value
        val next = when (intent) {
            is ProgramSetupIntent.SetBaseline ->
                current.copy(baselinePerDay = intent.value.coerceIn(1, 60))
            is ProgramSetupIntent.SetGoal -> current.copy(isQuitGoal = intent.isQuit)
            is ProgramSetupIntent.SetCutTo ->
                current.copy(cutToPerDay = intent.value.coerceIn(1, current.baselinePerDay - 1))
            is ProgramSetupIntent.SetDuration ->
                current.copy(durationWeeks = intent.weeks.coerceIn(2, 52))
            ProgramSetupIntent.Start -> {
                viewModelScope.launch { start(current) }
                return
            }
        }
        _state.value = rebuildState(next)
    }

    private fun rebuildState(s: ProgramSetupUiState): ProgramSetupUiState {
        val target = if (s.isQuitGoal) 0 else s.cutToPerDay.coerceIn(1, s.baselinePerDay - 1)
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val preview = buildPreview(s.baselinePerDay, target, s.durationWeeks, today)
        val canStart = s.baselinePerDay > target && s.durationWeeks >= 2
        return s.copy(weeklyPreview = preview, canStart = canStart)
    }

    private fun buildPreview(
        baseline: Int,
        target: Int,
        weeks: Int,
        startDate: LocalDate,
    ): List<WeekPreview> {
        val fakeProgram = Program(
            id = Uuid.parse("00000000-0000-0000-0000-000000000000"),
            baselinePerDay = baseline,
            targetPerDay = target,
            durationWeeks = weeks,
            startDate = startDate,
            strategyId = strategy.id,
            isActive = false,
        )
        return (1..weeks).map { w ->
            val date = startDate.plus((w - 1) * 7, DateTimeUnit.DAY)
            WeekPreview(week = w, allowance = strategy.allowanceForDay(fakeProgram, date))
        }
    }

    private suspend fun start(s: ProgramSetupUiState) {
        val target = if (s.isQuitGoal) 0 else s.cutToPerDay
        createProgram(
            baselinePerDay = s.baselinePerDay,
            targetPerDay = target,
            durationWeeks = s.durationWeeks,
        )
        _effects.send(ProgramSetupEffect.ProgramCreated)
    }
}

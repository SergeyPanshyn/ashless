package com.span.ashless.domain.usecase

import com.span.ashless.domain.model.CigaretteEntry
import com.span.ashless.domain.model.PacingTimerState
import com.span.ashless.domain.model.Program
import com.span.ashless.domain.model.WakingHours
import com.span.ashless.domain.reduction.ReductionStrategyRegistry
import com.span.ashless.domain.repository.EntryRepository
import com.span.ashless.domain.repository.ProgramRepository
import com.span.ashless.domain.repository.SettingsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.Instant

private fun defaultTicker(): Flow<Unit> =
    flow {
        while (true) {
            emit(Unit)
            delay(1_000L)
        }
    }

class ObservePacingTimer(
    private val entryRepository: EntryRepository,
    private val programRepository: ProgramRepository,
    private val settingsRepository: SettingsRepository,
    private val registry: ReductionStrategyRegistry,
    private val ticker: Flow<Unit> = defaultTicker(),
) {
    operator fun invoke(): Flow<PacingTimerState> =
        combine(
            entryRepository.observeMostRecentEntry(),
            entryRepository.observeTodayEntries(),
            programRepository.observeActiveProgram(),
            settingsRepository.observeWakingHours(),
            ticker,
        ) { lastEntry, todayEntries, program, waking, _ ->
            compute(lastEntry, todayEntries, program, waking)
        }

    private fun compute(
        lastEntry: CigaretteEntry?,
        todayEntries: List<CigaretteEntry>,
        program: Program?,
        waking: WakingHours,
    ): PacingTimerState {
        if (lastEntry == null) return PacingTimerState.Hidden
        val now: Instant = Clock.System.now()
        val minutesSinceLast = ((now - lastEntry.smokedAt).inWholeSeconds.coerceAtLeast(0) / 60).toInt()
        if (program == null) return PacingTimerState.ElapsedAwareness(minutesSinceLast)
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val allowanceToday = registry.get(program.strategyId).allowanceForDay(program, today)
        if (allowanceToday == 0 || todayEntries.size > allowanceToday) return PacingTimerState.Hidden
        val minutesRemaining = (waking.wakingMinutes / allowanceToday) - minutesSinceLast
        return if (minutesRemaining <= 0) {
            PacingTimerState.WindowOpen(minutesSinceLast)
        } else {
            PacingTimerState.Countdown(minutesRemaining)
        }
    }
}

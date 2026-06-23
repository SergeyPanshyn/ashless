package com.span.ashless.presentation.today

import com.span.ashless.domain.model.CigaretteEntry
import com.span.ashless.domain.model.Program
import com.span.ashless.domain.reduction.LinearWeeklyStepDownStrategy
import com.span.ashless.domain.reduction.ReductionStrategyRegistry
import com.span.ashless.domain.repository.EntryRepository
import com.span.ashless.domain.repository.ProgramRepository
import com.span.ashless.domain.usecase.DeleteEntry
import com.span.ashless.domain.usecase.LogCigarette
import com.span.ashless.domain.usecase.ObserveTodayState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class TodayViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()

    private var deletedId: Uuid? = null

    private val fakeObserveTodayState = ObserveTodayState(
        entryRepository = FakeEntryRepository(),
        programRepository = FakeProgramRepository(),
        registry = ReductionStrategyRegistry(listOf(LinearWeeklyStepDownStrategy())),
    )
    private val fakeLogCigarette = LogCigarette(repository = FakeEntryRepository())
    private val fakeDeleteEntry = DeleteEntry(
        repository = FakeEntryRepository(onDelete = { id -> deletedId = id }),
    )

    private lateinit var viewModel: TodayViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = TodayViewModel(fakeObserveTodayState, fakeLogCigarette, fakeDeleteEntry)
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialStateIsOnTrackWithIdleButton() =
        runTest(dispatcher) {
            val state = viewModel.state.value
            assertEquals(TodayStatusStyle.ON_TRACK, state.statusStyle)
            assertEquals(LogButtonState.Idle, state.buttonState)
        }

    @Test
    fun logIntentSetsButtonToLoggedState() =
        runTest(dispatcher) {
            viewModel.onIntent(TodayIntent.Log)

            assertIs<LogButtonState.Logged>(viewModel.state.value.buttonState)
        }

    @Test
    fun loggedButtonTimeLabelHasCorrectFormat() =
        runTest(dispatcher) {
            viewModel.onIntent(TodayIntent.Log)

            val btn = assertIs<LogButtonState.Logged>(viewModel.state.value.buttonState)
            assertEquals(5, btn.timeLabel.length)
            assertEquals(':', btn.timeLabel[2])
        }

    @Test
    fun undoIntentResetsButtonToIdle() =
        runTest(dispatcher) {
            viewModel.onIntent(TodayIntent.Log)
            assertIs<LogButtonState.Logged>(viewModel.state.value.buttonState)

            viewModel.onIntent(TodayIntent.Undo)

            assertEquals(LogButtonState.Idle, viewModel.state.value.buttonState)
        }

    @Test
    fun undoIntentCallsDeleteWithCorrectId() =
        runTest(dispatcher) {
            viewModel.onIntent(TodayIntent.Log)
            viewModel.onIntent(TodayIntent.Undo)

            assertEquals(FAKE_ENTRY_ID, deletedId)
        }

    @Test
    fun autoUndoTimeoutResetsButtonToIdle() =
        runTest(dispatcher) {
            viewModel.onIntent(TodayIntent.Log)
            assertIs<LogButtonState.Logged>(viewModel.state.value.buttonState)

            advanceTimeBy(5_001L)

            assertEquals(LogButtonState.Idle, viewModel.state.value.buttonState)
        }

    @Test
    fun undoAfterTimeoutDoesNotCallDelete() =
        runTest(dispatcher) {
            viewModel.onIntent(TodayIntent.Log)
            advanceTimeBy(5_001L)
            assertEquals(LogButtonState.Idle, viewModel.state.value.buttonState)

            viewModel.onIntent(TodayIntent.Undo)

            assertNull(deletedId)
        }
}

private val FAKE_ENTRY_ID = Uuid.parse("00000000-0000-0000-0000-000000000001")
private val FAKE_PROGRAM_START = LocalDate(2026, 1, 1)

private class FakeEntryRepository(
    private val onDelete: (Uuid) -> Unit = {},
) : EntryRepository {
    override suspend fun log(): CigaretteEntry =
        CigaretteEntry(
            id = FAKE_ENTRY_ID,
            smokedAt = kotlinx.datetime.Instant.fromEpochMilliseconds(0L),
        )

    override suspend fun delete(id: Uuid) = onDelete(id)

    override fun observeTodayEntries(): Flow<List<CigaretteEntry>> = flowOf(emptyList())

    override fun observeEntriesSince(from: Instant): Flow<List<CigaretteEntry>> = flowOf(emptyList())
}

private class FakeProgramRepository : ProgramRepository {
    override suspend fun save(program: Program) = Unit

    override fun observeActiveProgram(): Flow<Program?> =
        flowOf(
            Program(
                id = Uuid.parse("00000000-0000-0000-0000-000000000099"),
                baselinePerDay = 10,
                targetPerDay = 0,
                durationWeeks = 8,
                startDate = FAKE_PROGRAM_START,
                strategyId = "linear_weekly_step_down",
                isActive = true,
            ),
        )
}

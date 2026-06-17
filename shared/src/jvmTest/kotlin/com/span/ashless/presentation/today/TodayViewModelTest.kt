package com.span.ashless.presentation.today

import com.span.ashless.domain.model.CigaretteEntry
import com.span.ashless.domain.model.TodayState
import com.span.ashless.domain.usecase.DeleteEntry
import com.span.ashless.domain.usecase.LogCigarette
import com.span.ashless.domain.usecase.ObserveTodayState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class TodayViewModelTest {
    // UnconfinedTestDispatcher runs coroutines eagerly — no advanceUntilIdle() needed for non-delay code.
    // runTest(dispatcher) shares the virtual clock so advanceTimeBy() controls delay() in viewModelScope.
    private val dispatcher = UnconfinedTestDispatcher()

    private val todayStateFlow = MutableStateFlow(TodayState(count = 0))
    private var deletedId: String? = null
    // Fixed fake clock — avoids kotlinx-datetime runtime call in log() during tests
    private val fixedClock = object : Clock {
        override fun now(): Instant = Instant.fromEpochMilliseconds(0L)
    }

    private val fakeObserveTodayState = ObserveTodayState(
        repository = FakeEntryRepository(todayStateFlow = todayStateFlow),
    )
    private val fakeLogCigarette = LogCigarette(
        repository = FakeEntryRepository(todayStateFlow = todayStateFlow),
    )
    private val fakeDeleteEntry = DeleteEntry(
        repository = FakeEntryRepository(onDelete = { id -> deletedId = id }),
    )

    private lateinit var viewModel: TodayViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = TodayViewModel(fakeObserveTodayState, fakeLogCigarette, fakeDeleteEntry, fixedClock)
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialStateIsOnTrackWithIdleButton() = runTest(dispatcher) {
        val state = viewModel.state.value
        assertEquals(TodayStatusStyle.ON_TRACK, state.statusStyle)
        assertEquals(LogButtonState.Idle, state.buttonState)
    }

    @Test
    fun logIntentSetsButtonToLoggedState() = runTest(dispatcher) {
        viewModel.onIntent(TodayIntent.Log)

        assertIs<LogButtonState.Logged>(viewModel.state.value.buttonState)
    }

    @Test
    fun loggedButtonContainsHhMmTimeLabel() = runTest(dispatcher) {
        viewModel.onIntent(TodayIntent.Log)

        val btn = assertIs<LogButtonState.Logged>(viewModel.state.value.buttonState)
        assertEquals(5, btn.timeLabel.length)
        assertEquals(':', btn.timeLabel[2])
    }

    @Test
    fun undoIntentResetsButtonToIdle() = runTest(dispatcher) {
        viewModel.onIntent(TodayIntent.Log)
        assertIs<LogButtonState.Logged>(viewModel.state.value.buttonState)

        viewModel.onIntent(TodayIntent.Undo)

        assertEquals(LogButtonState.Idle, viewModel.state.value.buttonState)
    }

    @Test
    fun undoIntentCallsDeleteWithCorrectId() = runTest(dispatcher) {
        viewModel.onIntent(TodayIntent.Log)
        viewModel.onIntent(TodayIntent.Undo)

        assertEquals("fake-id", deletedId)
    }

    @Test
    fun autoUndoTimeoutResetsButtonToIdle() = runTest(dispatcher) {
        viewModel.onIntent(TodayIntent.Log)
        assertIs<LogButtonState.Logged>(viewModel.state.value.buttonState)

        advanceTimeBy(5_001L)

        assertEquals(LogButtonState.Idle, viewModel.state.value.buttonState)
    }

    @Test
    fun undoAfterTimeoutDoesNotCallDelete() = runTest(dispatcher) {
        viewModel.onIntent(TodayIntent.Log)
        advanceTimeBy(5_001L)
        assertEquals(LogButtonState.Idle, viewModel.state.value.buttonState)

        viewModel.onIntent(TodayIntent.Undo)

        assertNull(deletedId)
    }
}

private class FakeEntryRepository(
    private val todayStateFlow: MutableStateFlow<TodayState> = MutableStateFlow(TodayState()),
    private val onDelete: (String) -> Unit = {},
) : com.span.ashless.domain.repository.EntryRepository {
    override suspend fun log(): CigaretteEntry = CigaretteEntry(id = "fake-id", timestampMs = 0L)

    override suspend fun delete(id: String) = onDelete(id)

    override fun observeTodayEntries() = flowOf(emptyList<CigaretteEntry>())
}

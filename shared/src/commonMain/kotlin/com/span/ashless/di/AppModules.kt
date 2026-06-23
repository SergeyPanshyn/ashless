package com.span.ashless.di

import com.span.ashless.data.datasource.EntryDataSource
import com.span.ashless.data.datasource.ProgramDataSource
import com.span.ashless.data.local.RoomEntryDataSource
import com.span.ashless.data.local.RoomProgramDataSource
import com.span.ashless.data.repository.EntryRepositoryImpl
import com.span.ashless.data.repository.ProgramRepositoryImpl
import com.span.ashless.domain.reduction.LinearWeeklyStepDownStrategy
import com.span.ashless.domain.reduction.ReductionStrategyRegistry
import com.span.ashless.domain.repository.EntryRepository
import com.span.ashless.domain.repository.ProgramRepository
import com.span.ashless.domain.usecase.CreateProgram
import com.span.ashless.domain.usecase.DeleteEntry
import com.span.ashless.domain.usecase.LogCigarette
import com.span.ashless.domain.usecase.ObserveHistory
import com.span.ashless.domain.usecase.ObserveProgramProgress
import com.span.ashless.domain.usecase.ObserveStats
import com.span.ashless.domain.usecase.ObserveTodayState
import com.span.ashless.presentation.history.HistoryViewModel
import com.span.ashless.presentation.program.ProgramProgressViewModel
import com.span.ashless.presentation.program.ProgramSetupViewModel
import com.span.ashless.presentation.stats.StatsViewModel
import com.span.ashless.presentation.today.TodayViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val dataModule = module {
    single<EntryDataSource> { RoomEntryDataSource(get()) }
    single<EntryRepository> { EntryRepositoryImpl(get()) }
    single<ProgramDataSource> { RoomProgramDataSource(get()) }
    single<ProgramRepository> { ProgramRepositoryImpl(get()) }
}

val domainModule = module {
    single { ReductionStrategyRegistry(listOf(LinearWeeklyStepDownStrategy())) }
    factory { LogCigarette(get()) }
    factory { DeleteEntry(get()) }
    factory { ObserveTodayState(get(), get(), get()) }
    factory { CreateProgram(get()) }
    factory { ObserveProgramProgress(get(), get()) }
    factory { ObserveHistory(get()) }
    factory { ObserveStats(get(), get(), get()) }
}

val presentationModule = module {
    viewModel { TodayViewModel(get(), get(), get()) }
    viewModel { ProgramSetupViewModel(get()) }
    viewModel { ProgramProgressViewModel(get()) }
    viewModel { HistoryViewModel(get(), get()) }
    viewModel { StatsViewModel(get()) }
}

expect fun platformModule(): Module

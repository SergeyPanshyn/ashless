package com.span.ashless.di

import com.span.ashless.data.datasource.EntryDataSource
import com.span.ashless.data.local.RoomEntryDataSource
import com.span.ashless.data.repository.EntryRepositoryImpl
import com.span.ashless.domain.repository.EntryRepository
import com.span.ashless.domain.usecase.DeleteEntry
import com.span.ashless.domain.usecase.LogCigarette
import com.span.ashless.domain.usecase.ObserveTodayState
import com.span.ashless.presentation.today.TodayViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val dataModule = module {
    single<EntryDataSource> { RoomEntryDataSource(get()) }
    single<EntryRepository> { EntryRepositoryImpl(get()) }
}

val domainModule = module {
    factory { LogCigarette(get()) }
    factory { DeleteEntry(get()) }
    factory { ObserveTodayState(get()) }
}

val presentationModule = module {
    viewModel { TodayViewModel(get(), get(), get()) }
}

expect fun platformModule(): Module

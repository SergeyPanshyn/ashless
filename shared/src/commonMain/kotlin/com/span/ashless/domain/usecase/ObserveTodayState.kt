package com.span.ashless.domain.usecase

import com.span.ashless.domain.model.TodayState
import com.span.ashless.domain.repository.EntryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveTodayState(private val repository: EntryRepository) {
    operator fun invoke(): Flow<TodayState> =
        repository.observeTodayEntries().map { entries -> TodayState(count = entries.size) }
}

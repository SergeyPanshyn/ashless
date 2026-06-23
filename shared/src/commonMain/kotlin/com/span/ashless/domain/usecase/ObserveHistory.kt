package com.span.ashless.domain.usecase

import com.span.ashless.domain.model.CigaretteEntry
import com.span.ashless.domain.repository.EntryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import kotlin.time.Clock

class ObserveHistory(private val repository: EntryRepository) {
    operator fun invoke(daysBack: Int = 29): Flow<List<CigaretteEntry>> {
        val tz = TimeZone.currentSystemDefault()
        val startDate = Clock.System.todayIn(tz).minus(daysBack, DateTimeUnit.DAY)
        val from = startDate.atStartOfDayIn(tz)
        return repository.observeEntriesSince(from)
    }
}

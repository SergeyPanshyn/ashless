package com.span.ashless.domain.usecase

import com.span.ashless.domain.model.CigaretteEntry
import com.span.ashless.domain.repository.EntryRepository

class LogCigarette(private val repository: EntryRepository) {
    suspend operator fun invoke(): CigaretteEntry = repository.log()
}

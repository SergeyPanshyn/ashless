package com.span.ashless.domain.usecase

import com.span.ashless.domain.repository.EntryRepository
import kotlin.uuid.Uuid

class DeleteEntry(private val repository: EntryRepository) {
    suspend operator fun invoke(id: Uuid) = repository.delete(id)
}

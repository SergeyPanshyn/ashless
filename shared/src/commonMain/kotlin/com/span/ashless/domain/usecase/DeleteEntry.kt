package com.span.ashless.domain.usecase

import com.span.ashless.domain.repository.EntryRepository

class DeleteEntry(private val repository: EntryRepository) {
    suspend operator fun invoke(id: String) = repository.delete(id)
}

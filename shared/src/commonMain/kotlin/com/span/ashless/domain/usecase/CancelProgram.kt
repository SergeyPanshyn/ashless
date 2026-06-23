package com.span.ashless.domain.usecase

import com.span.ashless.domain.repository.ProgramRepository

class CancelProgram(private val repository: ProgramRepository) {
    suspend operator fun invoke() = repository.cancelActive()
}

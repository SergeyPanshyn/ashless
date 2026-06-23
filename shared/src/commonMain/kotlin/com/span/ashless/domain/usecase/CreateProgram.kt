package com.span.ashless.domain.usecase

import com.span.ashless.domain.model.Program
import com.span.ashless.domain.repository.ProgramRepository
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.uuid.Uuid

class CreateProgram(private val repository: ProgramRepository) {
    suspend operator fun invoke(
        baselinePerDay: Int,
        targetPerDay: Int,
        durationWeeks: Int,
        strategyId: String = "linear_weekly_step_down",
        startDate: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
    ) {
        require(baselinePerDay > targetPerDay) { "baseline must exceed target" }
        require(durationWeeks >= 2) { "duration must be at least 2 weeks" }
        val program = Program(
            id = Uuid.random(),
            baselinePerDay = baselinePerDay,
            targetPerDay = targetPerDay,
            durationWeeks = durationWeeks,
            startDate = startDate,
            strategyId = strategyId,
            isActive = true,
        )
        repository.save(program)
    }
}

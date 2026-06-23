package com.span.ashless.presentation.program

sealed interface ProgramSetupIntent {
    data class SetBaseline(val value: Int) : ProgramSetupIntent
    data class SetGoal(val isQuit: Boolean) : ProgramSetupIntent
    data class SetCutTo(val value: Int) : ProgramSetupIntent
    data class SetDuration(val weeks: Int) : ProgramSetupIntent
    data object Start : ProgramSetupIntent
}

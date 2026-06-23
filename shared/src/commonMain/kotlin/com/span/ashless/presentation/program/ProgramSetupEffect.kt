package com.span.ashless.presentation.program

sealed interface ProgramSetupEffect {
    data object ProgramCreated : ProgramSetupEffect
}

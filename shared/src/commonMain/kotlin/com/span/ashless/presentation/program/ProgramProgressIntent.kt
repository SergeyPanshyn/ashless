package com.span.ashless.presentation.program

sealed class ProgramProgressIntent {
    object CancelTapped : ProgramProgressIntent()
    object CancelConfirmed : ProgramProgressIntent()
    object CancelDismissed : ProgramProgressIntent()
}

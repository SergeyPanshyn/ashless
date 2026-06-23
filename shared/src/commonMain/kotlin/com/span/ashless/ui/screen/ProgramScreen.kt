package com.span.ashless.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.span.ashless.presentation.program.ProgramProgressViewModel
import com.span.ashless.presentation.program.ProgramSetupViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProgramScreen(modifier: Modifier = Modifier) {
    val setupVm: ProgramSetupViewModel = koinViewModel()
    val progressVm: ProgramProgressViewModel = koinViewModel()
    val progressState by progressVm.state.collectAsStateWithLifecycle()

    if (progressState.weekLabel.isEmpty()) {
        ProgramSetupScreen(
            onProgramCreated = {},
            modifier = modifier,
            viewModel = setupVm,
        )
    } else {
        ProgramProgressScreen(modifier = modifier, viewModel = progressVm)
    }
}

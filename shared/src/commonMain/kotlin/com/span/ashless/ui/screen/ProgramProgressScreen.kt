package com.span.ashless.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.span.ashless.presentation.program.ProgramProgressIntent
import com.span.ashless.presentation.program.ProgramProgressViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProgramProgressScreen(
    modifier: Modifier = Modifier,
    viewModel: ProgramProgressViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize().padding(horizontal = 24.dp),
    ) {
        Text(state.weekLabel, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text(state.allowanceLabel, style = MaterialTheme.typography.titleLarge)
        if (state.nextWeekLabel.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                state.nextWeekLabel,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            state.goalLabel,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            state.goalDateLabel,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(48.dp))
        OutlinedButton(
            onClick = { viewModel.onIntent(ProgramProgressIntent.CancelTapped) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Cancel program")
        }
    }

    if (state.showCancelConfirm) {
        AlertDialog(
            onDismissRequest = { viewModel.onIntent(ProgramProgressIntent.CancelDismissed) },
            title = { Text("Cancel program?") },
            text = {
                Text(
                    "Your progress will be lost and the daily allowance will be removed. Your logged entries are kept.",
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.onIntent(ProgramProgressIntent.CancelConfirmed) }) {
                    Text("Cancel program")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onIntent(ProgramProgressIntent.CancelDismissed) }) {
                    Text("Keep going")
                }
            },
        )
    }
}

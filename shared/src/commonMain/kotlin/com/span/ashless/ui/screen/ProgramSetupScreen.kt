package com.span.ashless.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.span.ashless.presentation.program.ProgramSetupEffect
import com.span.ashless.presentation.program.ProgramSetupIntent
import com.span.ashless.presentation.program.ProgramSetupUiState
import com.span.ashless.presentation.program.ProgramSetupViewModel
import com.span.ashless.presentation.program.WeekPreview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProgramSetupScreen(
    onProgramCreated: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProgramSetupViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                ProgramSetupEffect.ProgramCreated -> onProgramCreated()
            }
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize().padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Set up your program", style = MaterialTheme.typography.headlineMedium)
            Text(
                "We'll step you down gradually.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        item { BaselineSection(state, onIntent = viewModel::onIntent) }
        item { GoalSection(state, onIntent = viewModel::onIntent) }
        item { DurationSection(state, onIntent = viewModel::onIntent) }
        if (state.weeklyPreview.isNotEmpty()) {
            item {
                Text("Week-by-week preview", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
            }
            items(state.weeklyPreview) { preview ->
                WeekPreviewRow(preview)
            }
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { viewModel.onIntent(ProgramSetupIntent.Start) },
                enabled = state.canStart,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("Start program", style = MaterialTheme.typography.titleMedium)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun BaselineSection(
    state: ProgramSetupUiState,
    onIntent: (ProgramSetupIntent) -> Unit,
) {
    SectionCard(title = "How many do you smoke per day now?") {
        StepperRow(
            label = "${state.baselinePerDay}",
            onDecrement = { onIntent(ProgramSetupIntent.SetBaseline(state.baselinePerDay - 1)) },
            onIncrement = { onIntent(ProgramSetupIntent.SetBaseline(state.baselinePerDay + 1)) },
        )
    }
}

@Composable
private fun GoalSection(
    state: ProgramSetupUiState,
    onIntent: (ProgramSetupIntent) -> Unit,
) {
    SectionCard(title = "What's your goal?") {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = state.isQuitGoal,
                onClick = { onIntent(ProgramSetupIntent.SetGoal(true)) },
                label = { Text("Quit") },
            )
            FilterChip(
                selected = !state.isQuitGoal,
                onClick = { onIntent(ProgramSetupIntent.SetGoal(false)) },
                label = { Text("Cut down") },
            )
        }
        if (!state.isQuitGoal) {
            Spacer(modifier = Modifier.height(12.dp))
            Text("Cut to ${state.cutToPerDay}/day", style = MaterialTheme.typography.bodyMedium)
            StepperRow(
                label = "${state.cutToPerDay}",
                onDecrement = { onIntent(ProgramSetupIntent.SetCutTo(state.cutToPerDay - 1)) },
                onIncrement = { onIntent(ProgramSetupIntent.SetCutTo(state.cutToPerDay + 1)) },
            )
        }
    }
}

@Composable
private fun DurationSection(
    state: ProgramSetupUiState,
    onIntent: (ProgramSetupIntent) -> Unit,
) {
    SectionCard(title = "Duration: ${state.durationWeeks} weeks") {
        Slider(
            value = state.durationWeeks.toFloat(),
            onValueChange = { onIntent(ProgramSetupIntent.SetDuration(it.toInt())) },
            valueRange = 2f..52f,
            steps = 49,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun SectionCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun StepperRow(
    label: String,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onDecrement) {
            Text("−", style = MaterialTheme.typography.headlineSmall)
        }
        Text(label, style = MaterialTheme.typography.headlineMedium)
        IconButton(onClick = onIncrement) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "increase",
                modifier = Modifier.size(28.dp),
            )
        }
    }
}

@Composable
private fun WeekPreviewRow(
    preview: WeekPreview,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            "Week ${preview.week}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text("${preview.allowance}/day", style = MaterialTheme.typography.bodyMedium)
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
}

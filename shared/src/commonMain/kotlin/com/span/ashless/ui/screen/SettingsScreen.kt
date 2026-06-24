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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.span.ashless.presentation.settings.SettingsIntent
import com.span.ashless.presentation.settings.SettingsViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Waking hours",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(8.dp))
        HourStepper(
            label = "Wake up",
            hour = state.wakeHour,
            onIncrement = { viewModel.onIntent(SettingsIntent.IncrementWakeHour) },
            onDecrement = { viewModel.onIntent(SettingsIntent.DecrementWakeHour) },
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
        HourStepper(
            label = "Bedtime",
            hour = state.bedHour,
            onIncrement = { viewModel.onIntent(SettingsIntent.IncrementBedHour) },
            onDecrement = { viewModel.onIntent(SettingsIntent.DecrementBedHour) },
        )
    }
}

@Composable
private fun HourStepper(
    label: String,
    hour: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth().padding(vertical = 8.dp),
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onDecrement, modifier = Modifier.size(40.dp)) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Decrease")
            }
            Text(
                text = "${hour.toString().padStart(2, '0')}:00",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 8.dp),
            )
            IconButton(onClick = onIncrement, modifier = Modifier.size(40.dp)) {
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Increase")
            }
        }
    }
}

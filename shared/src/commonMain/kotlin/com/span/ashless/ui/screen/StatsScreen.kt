package com.span.ashless.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.span.ashless.presentation.stats.BarData
import com.span.ashless.presentation.stats.StatsUiState
import com.span.ashless.presentation.stats.StatsViewModel
import com.span.ashless.presentation.stats.SummaryCard
import com.span.ashless.ui.theme.LocalAshlessStatusColors
import org.koin.compose.viewmodel.koinViewModel

private val CHART_HEIGHT = 180.dp
private val BAR_CORNER = 4.dp

@Composable
fun StatsScreen(
    modifier: Modifier = Modifier,
    viewModel: StatsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Text("Last 7 days", style = MaterialTheme.typography.titleLarge)
        BarChart(bars = state.bars)
        SummaryRow(state = state)
        if (state.programLabel.isNotEmpty()) {
            ProgramBadge(label = state.programLabel)
        }
    }
}

@Composable
private fun BarChart(
    bars: List<BarData>,
    modifier: Modifier = Modifier,
) {
    if (bars.isEmpty()) return
    val maxValue = bars.maxOf { maxOf(it.count, it.allowance ?: 0) }.coerceAtLeast(1)
    val primary = MaterialTheme.colorScheme.primary
    val overLimitColor = LocalAshlessStatusColors.current.overLimitText

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().height(CHART_HEIGHT),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            bars.forEach { bar ->
                BarColumn(
                    bar = bar,
                    maxValue = maxValue,
                    activeColor = if (bar.isOverLimit) overLimitColor else primary,
                    dimColor = primary.copy(alpha = 0.35f),
                    modifier = Modifier.weight(1f),
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            bars.forEach { bar ->
                Text(
                    text = bar.label,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    color = if (bar.isToday) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun BarColumn(
    bar: BarData,
    maxValue: Int,
    activeColor: Color,
    dimColor: Color,
    modifier: Modifier = Modifier,
) {
    val fraction = (bar.count.toFloat() / maxValue).coerceIn(0f, 1f)
    val color = if (bar.isToday || bar.isOverLimit) activeColor else dimColor

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(modifier = Modifier.height(16.dp), contentAlignment = Alignment.BottomCenter) {
            if (bar.count > 0) {
                Text(
                    text = bar.count.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = color,
                )
            }
        }
        val emptyFraction = (1f - fraction).coerceAtLeast(0f)
        if (emptyFraction > 0f) {
            Spacer(modifier = Modifier.weight(emptyFraction))
        }
        Box(
            modifier = Modifier
                .weight(fraction.coerceAtLeast(0.02f))
                .fillMaxWidth()
                .padding(horizontal = 2.dp)
                .background(
                    color = color,
                    shape = RoundedCornerShape(topStart = BAR_CORNER, topEnd = BAR_CORNER),
                ),
        )
    }
}

@Composable
private fun SummaryRow(
    state: StatsUiState,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        state.summaryCards.forEach { card ->
            SummaryCardItem(card = card, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun SummaryCardItem(
    card: SummaryCard,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(card.value, style = MaterialTheme.typography.headlineSmall)
            Text(
                card.title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ProgramBadge(
    label: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = label,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier,
    )
}

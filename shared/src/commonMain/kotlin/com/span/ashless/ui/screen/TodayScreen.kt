package com.span.ashless.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.span.ashless.presentation.today.LogButtonState
import com.span.ashless.presentation.today.TodayIntent
import com.span.ashless.presentation.today.TodayStatusStyle
import com.span.ashless.presentation.today.TodayViewModel
import com.span.ashless.ui.theme.AshlessStatusColors
import com.span.ashless.ui.theme.LocalAshlessStatusColors
import org.koin.compose.viewmodel.koinViewModel

private const val RING_STROKE_DP = 20f
private const val RING_SIZE_DP = 280f

// Color lookup table — pure mapping, no logic
private fun TodayStatusStyle.ringColor(
    s: AshlessStatusColors,
    primary: Color,
): Color =
    when (this) {
        TodayStatusStyle.ON_TRACK -> primary
        TodayStatusStyle.OVER_LIMIT -> s.overLimitText
        TodayStatusStyle.NO_PROGRAM -> primary.copy(alpha = 0.3f)
    }

private fun TodayStatusStyle.chipBackground(s: AshlessStatusColors): Color =
    when (this) {
        TodayStatusStyle.ON_TRACK -> s.onTrackContainer
        TodayStatusStyle.OVER_LIMIT -> s.overLimitContainer
        TodayStatusStyle.NO_PROGRAM -> s.onTrackContainer.copy(alpha = 0.4f)
    }

private fun TodayStatusStyle.chipTextColor(s: AshlessStatusColors): Color =
    when (this) {
        TodayStatusStyle.ON_TRACK -> s.onTrackText
        TodayStatusStyle.OVER_LIMIT -> s.overLimitText
        TodayStatusStyle.NO_PROGRAM -> s.onTrackText.copy(alpha = 0.6f)
    }

@Composable
fun TodayScreen(
    modifier: Modifier = Modifier,
    viewModel: TodayViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val statusColors = LocalAshlessStatusColors.current
    val primary = MaterialTheme.colorScheme.primary

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize().padding(horizontal = 24.dp),
    ) {
        CountRing(
            remainingCount = state.remainingCount,
            ringProgress = state.ringProgress,
            ringColor = state.statusStyle.ringColor(statusColors, primary),
        )
        Spacer(modifier = Modifier.height(16.dp))
        StatusChip(
            label = state.statusLabel,
            background = state.statusStyle.chipBackground(statusColors),
            textColor = state.statusStyle.chipTextColor(statusColors),
        )
        if (state.footerText.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = state.footerText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(modifier = Modifier.height(48.dp))
        LogArea(buttonState = state.buttonState, onIntent = viewModel::onIntent)
    }
}

@Composable
private fun CountRing(
    remainingCount: Int,
    ringProgress: Float,
    ringColor: Color,
    modifier: Modifier = Modifier,
) {
    val trackColor = MaterialTheme.colorScheme.outlineVariant
    val strokePx = RING_STROKE_DP.dp

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(RING_SIZE_DP.dp),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val inset = strokePx.toPx() / 2f
            val arcSize = Size(size.width - strokePx.toPx(), size.height - strokePx.toPx())
            val topLeft = Offset(inset, inset)
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx.toPx(), cap = StrokeCap.Round),
            )
            if (ringProgress > 0f) {
                drawArc(
                    color = ringColor,
                    startAngle = -90f,
                    sweepAngle = ringProgress * 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokePx.toPx(), cap = StrokeCap.Round),
                )
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = remainingCount.toString(),
                style = MaterialTheme.typography.displayLarge,
                color = ringColor,
            )
            Text(
                text = "left",
                style = MaterialTheme.typography.labelMedium,
                color = ringColor.copy(alpha = 0.7f),
            )
        }
    }
}

@Composable
private fun StatusChip(
    label: String,
    background: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = background,
        shape = RoundedCornerShape(999.dp),
        modifier = modifier,
    ) {
        Text(
            text = label,
            color = textColor,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
    }
}

@Composable
private fun LogArea(
    buttonState: LogButtonState,
    onIntent: (TodayIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(
        targetState = buttonState,
        label = "log_button_state",
        modifier = modifier.fillMaxWidth(),
    ) { btn ->
        when (btn) {
            is LogButtonState.Logged -> {
                OutlinedButton(
                    onClick = { onIntent(TodayIntent.Undo) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Logged ${btn.timeLabel}",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = "Undo",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
            LogButtonState.Idle -> {
                Button(
                    onClick = { onIntent(TodayIntent.Log) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Log",
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
        }
    }
}

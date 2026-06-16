package com.span.ashless.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class AshlessStatusColors(
    val onTrackContainer: Color,
    val onTrackText: Color,
    val overLimitContainer: Color,
    val overLimitText: Color,
)

val LightStatusColors = AshlessStatusColors(
    onTrackContainer = Teal50,
    onTrackText = Teal600,
    overLimitContainer = Amber50,
    overLimitText = Amber600,
)

val DarkStatusColors = AshlessStatusColors(
    onTrackContainer = Teal800,
    onTrackText = Teal100,
    overLimitContainer = Amber800,
    overLimitText = Amber100,
)

val LocalAshlessStatusColors = staticCompositionLocalOf { LightStatusColors }

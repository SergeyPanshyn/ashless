package com.span.ashless.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val LightColors = lightColorScheme(
    primary = Teal600,
    onPrimary = Teal50,
    primaryContainer = Teal50,
    onPrimaryContainer = Teal600,
    background = Neutral50,
    onBackground = NeutralInk,
    surface = Neutral0,
    onSurface = NeutralInk,
    surfaceVariant = Neutral100,
    onSurfaceVariant = Neutral600,
    outline = Neutral300,
    outlineVariant = Neutral200,
)

private val DarkColors = darkColorScheme(
    primary = Teal200,
    onPrimary = Teal900,
    primaryContainer = Teal800,
    onPrimaryContainer = Teal100,
    background = Neutral950,
    onBackground = Neutral100,
    surface = Neutral900,
    onSurface = Neutral100,
    surfaceVariant = Neutral850,
    onSurfaceVariant = Neutral300,
    outline = Neutral700,
    outlineVariant = Neutral800,
)

@Composable
fun AshlessTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    val statusColors = if (darkTheme) DarkStatusColors else LightStatusColors

    CompositionLocalProvider(LocalAshlessStatusColors provides statusColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AshlessTypography,
            content = content,
        )
    }
}

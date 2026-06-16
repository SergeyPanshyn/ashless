package com.span.ashless

import androidx.compose.runtime.Composable
import com.span.ashless.ui.navigation.AshlessNavHost
import com.span.ashless.ui.theme.AshlessTheme

@Composable
fun App() {
    AshlessTheme {
        AshlessNavHost()
    }
}

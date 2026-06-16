package com.span.ashless.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.span.ashless.ui.screen.HistoryScreen
import com.span.ashless.ui.screen.ProgramScreen
import com.span.ashless.ui.screen.StatsScreen
import com.span.ashless.ui.screen.TodayScreen

private data class NavTab(
    val screen: Screen,
    val label: String,
    val iconText: String,
)

private val navTabs = listOf(
    NavTab(Screen.Today, "Today", "◉"),
    NavTab(Screen.History, "History", "≡"),
    NavTab(Screen.Stats, "Stats", "↑"),
    NavTab(Screen.Program, "Program", "⊞"),
)

@Composable
fun AshlessNavHost(modifier: Modifier = Modifier) {
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar {
                navTabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        icon = {
                            Text(
                                text = tab.iconText,
                                style = MaterialTheme.typography.titleMedium,
                            )
                        },
                        label = { Text(tab.label) },
                    )
                }
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when (selectedIndex) {
                0 -> TodayScreen()
                1 -> HistoryScreen()
                2 -> StatsScreen()
                else -> ProgramScreen()
            }
        }
    }
}

package com.span.ashless.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.span.ashless.ui.icons.ArcheryIcon
import com.span.ashless.ui.icons.ChartIcon
import com.span.ashless.ui.icons.HomeIcon
import com.span.ashless.ui.icons.PlanIcon
import com.span.ashless.ui.icons.SettingsIcon
import com.span.ashless.ui.screen.HistoryScreen
import com.span.ashless.ui.screen.ProgramScreen
import com.span.ashless.ui.screen.SettingsScreen
import com.span.ashless.ui.screen.StatsScreen
import com.span.ashless.ui.screen.TodayScreen

private data class NavTab(
    val screen: Screen,
    val label: String,
    val icon: ImageVector,
)

private val navTabs = listOf(
    NavTab(Screen.Today, "Today", HomeIcon),
    NavTab(Screen.History, "History", ArcheryIcon),
    NavTab(Screen.Stats, "Stats", ChartIcon),
    NavTab(Screen.Program, "Program", PlanIcon),
    NavTab(Screen.Settings, "Settings", SettingsIcon),
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
                            Icon(modifier = Modifier.size(24.dp), imageVector = tab.icon, contentDescription = tab.label)
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
                3 -> ProgramScreen()
                else -> SettingsScreen()
            }
        }
    }
}

package com.span.ashless.ui.navigation

internal sealed class Screen(val route: String) {
    object Today : Screen("today")
    object History : Screen("history")
    object Stats : Screen("stats")
    object Program : Screen("program")
    object Settings : Screen("settings")
}

package com.span.ashless.presentation.settings

sealed class SettingsIntent {
    object IncrementWakeHour : SettingsIntent()
    object DecrementWakeHour : SettingsIntent()
    object IncrementBedHour : SettingsIntent()
    object DecrementBedHour : SettingsIntent()
}

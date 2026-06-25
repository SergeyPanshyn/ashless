package com.span.ashless.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.span.ashless.domain.model.WakingHours
import com.span.ashless.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.observeWakingHours().collect { waking ->
                _state.value = SettingsUiState(wakeHour = waking.wakeHour, bedHour = waking.bedHour)
            }
        }
    }

    fun onIntent(intent: SettingsIntent) {
        when (intent) {
            SettingsIntent.IncrementWakeHour -> adjustWakeHour(+1)
            SettingsIntent.DecrementWakeHour -> adjustWakeHour(-1)
            SettingsIntent.IncrementBedHour -> adjustBedHour(+1)
            SettingsIntent.DecrementBedHour -> adjustBedHour(-1)
        }
    }

    private fun adjustWakeHour(delta: Int) {
        val current = _state.value
        val newWake = (current.wakeHour + delta)
            .coerceIn(WakingHours.MIN_WAKE_HOUR, current.bedHour - WakingHours.MIN_GAP)
        save(newWake, current.bedHour)
    }

    private fun adjustBedHour(delta: Int) {
        val current = _state.value
        val newBed = (current.bedHour + delta)
            .coerceIn(current.wakeHour + WakingHours.MIN_GAP, WakingHours.MAX_BED_HOUR)
        save(current.wakeHour, newBed)
    }

    private fun save(
        wakeHour: Int,
        bedHour: Int,
    ) {
        viewModelScope.launch {
            settingsRepository.saveWakingHours(WakingHours(wakeHour = wakeHour, bedHour = bedHour))
        }
    }
}

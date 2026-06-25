package com.span.ashless.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.span.ashless.domain.model.WakingHours
import com.span.ashless.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val WAKE_HOUR_KEY = intPreferencesKey("wake_hour")
private val BED_HOUR_KEY = intPreferencesKey("bed_hour")

class DataStoreSettingsRepository(
    private val dataStore: DataStore<Preferences>,
) : SettingsRepository {
    override fun observeWakingHours(): Flow<WakingHours> =
        dataStore.data.map { prefs ->
            WakingHours(
                wakeHour = prefs[WAKE_HOUR_KEY] ?: WakingHours.DEFAULT.wakeHour,
                bedHour = prefs[BED_HOUR_KEY] ?: WakingHours.DEFAULT.bedHour,
            )
        }

    override suspend fun saveWakingHours(wakingHours: WakingHours) {
        dataStore.edit { prefs ->
            prefs[WAKE_HOUR_KEY] = wakingHours.wakeHour
            prefs[BED_HOUR_KEY] = wakingHours.bedHour
        }
    }
}

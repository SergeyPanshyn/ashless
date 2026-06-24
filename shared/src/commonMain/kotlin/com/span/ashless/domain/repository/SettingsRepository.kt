package com.span.ashless.domain.repository

import com.span.ashless.domain.model.WakingHours
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeWakingHours(): Flow<WakingHours>
    suspend fun saveWakingHours(wakingHours: WakingHours)
}

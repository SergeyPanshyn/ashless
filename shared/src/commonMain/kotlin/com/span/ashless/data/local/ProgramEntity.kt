package com.span.ashless.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "programs")
data class ProgramEntity(
    @PrimaryKey val id: String,
    val baselinePerDay: Int,
    val targetPerDay: Int,
    val durationWeeks: Int,
    val startDate: String,
    val strategyId: String,
    val isActive: Int,
)

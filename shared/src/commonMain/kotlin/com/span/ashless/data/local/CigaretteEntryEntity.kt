package com.span.ashless.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cigarette_entries")
data class CigaretteEntryEntity(
    @PrimaryKey val id: String,
    val timestampMs: Long,
)

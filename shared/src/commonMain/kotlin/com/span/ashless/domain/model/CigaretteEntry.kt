package com.span.ashless.domain.model

import kotlinx.datetime.Instant
import kotlin.uuid.Uuid

data class CigaretteEntry(
    val id: Uuid,
    val smokedAt: Instant,
)

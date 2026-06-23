package com.span.ashless.domain.reduction

class ReductionStrategyRegistry(strategies: List<ReductionStrategy>) {
    private val map = strategies.associateBy { it.id }

    fun get(id: String): ReductionStrategy = map[id] ?: error("Unknown strategy: $id")
}

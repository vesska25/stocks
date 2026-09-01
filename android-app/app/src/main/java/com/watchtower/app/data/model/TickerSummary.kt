package com.watchtower.app.data.model

import kotlinx.serialization.Serializable

/** Mirrors GET /api/tickers — see api/README.md in the repo root. */
@Serializable
data class TickerSummary(
    val ticker: String,
    val name: String? = null,
    val industry: String? = null,
    val price: Double? = null,
    val changePct: Double? = null,
    val compositeScore: Double? = null,
    val fundamentalsScore: Double? = null,
)

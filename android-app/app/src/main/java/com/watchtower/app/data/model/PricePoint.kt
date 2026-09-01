package com.watchtower.app.data.model

import kotlinx.serialization.Serializable

/** Mirrors GET /api/tickers/{ticker}/history — date is ISO "yyyy-MM-dd". */
@Serializable
data class PricePoint(
    val date: String,
    val close: Double? = null,
)

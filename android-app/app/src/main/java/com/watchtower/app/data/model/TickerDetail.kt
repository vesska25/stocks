package com.watchtower.app.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/** Mirrors GET /api/tickers/{ticker} — see api/README.md in the repo root. */
@Serializable
data class TickerDetail(
    val ticker: String,
    val name: String? = null,
    val industry: String? = null,
    val quote: Quote? = null,
    val technicals: Technicals? = null,
    val fundamentals: Fundamentals? = null,
) {
    @Serializable
    data class Quote(
        val price: Double? = null,
        val change: Double? = null,
        val changePct: Double? = null,
        val high: Double? = null,
        val low: Double? = null,
        val open: Double? = null,
        val previousClose: Double? = null,
        val quoteTimestamp: String? = null,
    )

    @Serializable
    data class Technicals(
        val compositeScore: Double? = null,
        val sma20: Double? = null,
        val sma50: Double? = null,
        val sma90: Double? = null,
        val sma200: Double? = null,
        val ema12: Double? = null,
        val ema26: Double? = null,
        val rsi14: Double? = null,
        val macd: Double? = null,
        val macdSignal: Double? = null,
        val macdHistogram: Double? = null,
        val volumeRatio20d: Double? = null,
        val high52w: Double? = null,
        val low52w: Double? = null,
        val positionInRange: Double? = null,
        // Shape of this column is defined by the n8n pipeline, not fixed by
        // this API — kept dynamic and parsed defensively in the UI.
        val signals: JsonElement? = null,
        val computedAt: String? = null,
    )

    @Serializable
    data class Fundamentals(
        val peRatio: Double? = null,
        val pbRatio: Double? = null,
        val revenueGrowthYoy: Double? = null,
        val profitMargin: Double? = null,
        val forwardEpsEstimate: Double? = null,
        val fundamentalsScore: Double? = null,
        val epsSurpriseLast4: JsonElement? = null,
        val fundamentalsSignals: JsonElement? = null,
    )
}

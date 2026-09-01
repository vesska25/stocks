package com.watchtower.app.data.model

import kotlinx.serialization.Serializable

/** Mirrors one entry of GET /api/digests's "content" array. */
@Serializable
data class DigestSummary(
    val id: Long,
    val digestText: String,
    val tickers: List<String> = emptyList(),
    val tickerSnapshots: List<TickerSnapshot> = emptyList(),
    val createdAt: String? = null,
) {
    @Serializable
    data class TickerSnapshot(
        val ticker: String,
        val compositeScore: Int? = null,
        val fundamentalsScore: Int? = null,
        val realtimePrice: Double? = null,
    )
}

/** Mirrors the Spring Page<DigestSummary> envelope from GET /api/digests. Unknown fields (pageable, sort, ...) are ignored by the Json config. */
@Serializable
data class DigestPage(
    val content: List<DigestSummary> = emptyList(),
    val totalPages: Int = 0,
    val totalElements: Long = 0,
    val number: Int = 0,
    val last: Boolean = true,
)

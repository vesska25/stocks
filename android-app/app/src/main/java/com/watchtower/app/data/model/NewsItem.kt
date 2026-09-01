package com.watchtower.app.data.model

import kotlinx.serialization.Serializable

/**
 * Mirrors GET /api/tickers/{ticker}/news. An empty list from the endpoint is
 * the expected common case (ticker_news is only populated for tickers whose
 * composite_score passed the daily threshold), not an error.
 */
@Serializable
data class NewsItem(
    val id: Long,
    val headline: String,
    val summary: String? = null,
    val source: String? = null,
    val newsDatetime: String? = null,
)

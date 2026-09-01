package com.watchtower.app.data

import com.watchtower.app.data.model.DigestPage
import com.watchtower.app.data.model.NewsItem
import com.watchtower.app.data.model.PricePoint
import com.watchtower.app.data.model.TickerDetail
import com.watchtower.app.data.model.TickerSummary
import com.watchtower.app.data.network.WatchtowerApi

/** Thin wrapper turning network exceptions into Result so screens can render a retry state. */
class WatchtowerRepository(private val api: WatchtowerApi) {

    suspend fun getTickers(): Result<List<TickerSummary>> = runCatching { api.getTickers() }

    suspend fun getTickerDetail(ticker: String): Result<TickerDetail> =
        runCatching { api.getTickerDetail(ticker) }

    suspend fun getTickerHistory(ticker: String, range: String): Result<List<PricePoint>> =
        runCatching { api.getTickerHistory(ticker, range) }

    suspend fun getTickerNews(ticker: String): Result<List<NewsItem>> =
        runCatching { api.getTickerNews(ticker) }

    suspend fun getDigests(page: Int, size: Int = 20): Result<DigestPage> =
        runCatching { api.getDigests(page, size) }
}

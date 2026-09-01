package com.watchtower.app.data.network

import com.watchtower.app.data.model.DigestPage
import com.watchtower.app.data.model.NewsItem
import com.watchtower.app.data.model.PricePoint
import com.watchtower.app.data.model.TickerDetail
import com.watchtower.app.data.model.TickerSummary
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/** Matches the endpoints documented in api/README.md at the repo root. */
interface WatchtowerApi {

    @GET("api/tickers")
    suspend fun getTickers(): List<TickerSummary>

    @GET("api/tickers/{ticker}")
    suspend fun getTickerDetail(@Path("ticker") ticker: String): TickerDetail

    @GET("api/tickers/{ticker}/history")
    suspend fun getTickerHistory(
        @Path("ticker") ticker: String,
        @Query("range") range: String,
    ): List<PricePoint>

    @GET("api/tickers/{ticker}/news")
    suspend fun getTickerNews(@Path("ticker") ticker: String): List<NewsItem>

    @GET("api/digests")
    suspend fun getDigests(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): DigestPage
}

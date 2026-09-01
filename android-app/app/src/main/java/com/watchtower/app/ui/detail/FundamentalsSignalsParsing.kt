package com.watchtower.app.ui.detail

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

data class IndustryAverages(
    val peRatio: Double?,
    val pbRatio: Double?,
    val profitMargin: Double?,
    val revenueGrowthYoy: Double?,
)

data class FundamentalsPeerComparison(
    val industry: String?,
    val peerTickers: List<String>,
    val averages: IndustryAverages?,
    val peVsIndustry: Int?,
    val pbVsIndustry: Int?,
    val marginVsIndustry: Int?,
    val growthVsIndustry: Int?,
)

/**
 * company_fundamentals.fundamentals_signals is a JSONB column shaped by the
 * n8n pipeline, not fixed by this API — parsed defensively, same approach as
 * SignalsParsing.kt for the technicals side. Null when fundamentalsScore
 * itself is null (ticker is the only one in its industry, nothing to compare
 * against), so a missing/malformed shape just means no peers section shows.
 */
fun parseFundamentalsSignals(element: JsonElement?): FundamentalsPeerComparison? {
    val obj = element as? JsonObject ?: return null
    return runCatching {
        val averagesObj = obj["industry_averages"] as? JsonObject
        val averages = averagesObj?.let {
            IndustryAverages(
                peRatio = it["pe_ratio"]?.jsonPrimitive?.doubleOrNull,
                pbRatio = it["pb_ratio"]?.jsonPrimitive?.doubleOrNull,
                profitMargin = it["profit_margin"]?.jsonPrimitive?.doubleOrNull,
                revenueGrowthYoy = it["revenue_growth_yoy"]?.jsonPrimitive?.doubleOrNull,
            )
        }
        FundamentalsPeerComparison(
            industry = obj["industry"]?.jsonPrimitive?.contentOrNull,
            peerTickers = obj["peer_tickers"]?.jsonArray?.mapNotNull { it.jsonPrimitive.contentOrNull } ?: emptyList(),
            averages = averages,
            peVsIndustry = obj["peVsIndustry"]?.jsonPrimitive?.intOrNull,
            pbVsIndustry = obj["pbVsIndustry"]?.jsonPrimitive?.intOrNull,
            marginVsIndustry = obj["marginVsIndustry"]?.jsonPrimitive?.intOrNull,
            growthVsIndustry = obj["growthVsIndustry"]?.jsonPrimitive?.intOrNull,
        )
    }.getOrNull()
}

package com.watchtower.app.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.watchtower.app.WatchtowerApplication
import com.watchtower.app.data.model.NewsItem
import com.watchtower.app.data.model.TickerDetail
import com.watchtower.app.ui.components.ScoreRing
import com.watchtower.app.ui.components.ScoreRingSizeDetail
import com.watchtower.app.ui.components.Sparkline
import com.watchtower.app.ui.components.bottomHairline
import com.watchtower.app.ui.components.formatChangePct
import com.watchtower.app.ui.components.formatPrice
import com.watchtower.app.ui.components.formatRelativeTime
import com.watchtower.app.ui.components.orBlankIfLiteralNull
import com.watchtower.app.ui.theme.AccentTint11
import com.watchtower.app.ui.theme.HairlineFaint
import com.watchtower.app.ui.theme.HairlineOnDark
import com.watchtower.app.ui.theme.RingInnerFundamentals
import com.watchtower.app.ui.theme.RingOuterTechnical
import com.watchtower.app.ui.theme.ScreenBg
import com.watchtower.app.ui.theme.TextBody
import com.watchtower.app.ui.theme.TextMuted
import com.watchtower.app.ui.theme.TextPrimary
import com.watchtower.app.ui.theme.arrowColor
import com.watchtower.app.ui.theme.arrowGlyph
import com.watchtower.app.ui.theme.directionColor

@Composable
fun DetailScreen(ticker: String, onBack: () -> Unit) {
    val app = LocalContext.current.applicationContext as WatchtowerApplication
    val factory = viewModelFactory { initializer { DetailViewModel(app.repository, ticker) } }
    val viewModel: DetailViewModel = viewModel(factory = factory)
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().background(ScreenBg)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = RingOuterTechnical)
            }
            Column {
                Text(ticker, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                state.detail?.name.orBlankIfLiteralNull()?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall, color = TextMuted)
                }
            }
        }

        when {
            state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = RingOuterTechnical)
            }
            state.error != null || state.detail == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Couldn't load $ticker: ${state.error}", color = TextMuted)
                    Spacer(Modifier.height(8.dp))
                    Text("Tap to retry", color = RingOuterTechnical, modifier = Modifier.clickable { viewModel.loadAll() })
                }
            }
            else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                item { PriceHeader(state.detail!!) }
                item {
                    Column(Modifier.padding(horizontal = 16.dp)) {
                        Sparkline(state.visibleHistory, state.sma50)
                        if (state.sma50.isNotEmpty()) {
                            Text(
                                "— — SMA50",
                                color = TextMuted,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(top = 4.dp),
                            )
                        }
                        RangeChips(state.range, viewModel::setRange)
                    }
                }
                item { ScoresCard(state.detail!!, state.signalsOpen, viewModel::toggleSignals) }
                item { FundamentalsSection(state.detail!!.fundamentals) }
                item { EarningsSection(state.detail!!.fundamentals) }
                item { NewsSection(state.news) }
                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
private fun PriceHeader(detail: TickerDetail) {
    val quote = detail.quote
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
        Column {
            Text(formatPrice(quote?.price), style = MaterialTheme.typography.titleLarge, color = TextPrimary)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(arrowGlyph(quote?.changePct), color = arrowColor(quote?.changePct))
                Text(formatChangePct(quote?.changePct), color = directionColor(quote?.changePct))
                Spacer(Modifier.width(8.dp))
                Text("today", color = TextMuted, style = MaterialTheme.typography.bodySmall)
            }
        }
        Text(
            "DELAYED · ${formatRelativeTime(quote?.quoteTimestamp)}",
            color = TextMuted,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@Composable
private fun RangeChips(current: String, onSelect: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        listOf("1D", "1W", "1M", "1Y").forEach { range ->
            val active = range == current
            Text(
                range,
                color = if (active) TextPrimary else TextMuted,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier
                    .weight(1f)
                    .background(if (active) AccentTint11 else Color.Transparent)
                    .border(1.dp, if (active) RingOuterTechnical else HairlineOnDark)
                    .clickable { onSelect(range) }
                    .padding(vertical = 8.dp),
            )
        }
    }
}

@Composable
private fun ScoresCard(detail: TickerDetail, signalsOpen: Boolean, onToggleSignals: () -> Unit) {
    val technicals = detail.technicals
    val fundamentals = detail.fundamentals
    val signals = parseSignals(technicals?.signals)
    val positiveCount = signals.count { signalPointsSign(it.points) > 0 }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(1.dp, HairlineOnDark)
            .padding(16.dp),
    ) {
        Text("SCORES", color = TextMuted, style = MaterialTheme.typography.labelSmall)
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            ScoreRing(technicals?.compositeScore, fundamentals?.fundamentalsScore, size = ScoreRingSizeDetail)
            Spacer(Modifier.width(16.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("●", color = RingOuterTechnical, style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        technicals?.compositeScore?.let { (if (it >= 0) "+" else "") + it.toInt() } ?: "—",
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(" / +7 technical", color = TextMuted, style = MaterialTheme.typography.labelSmall)
                }
                Text(
                    if (signals.isNotEmpty()) "$positiveCount of ${signals.size} signals positive" else "No technical signals available",
                    color = TextMuted,
                    style = MaterialTheme.typography.bodySmall,
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("●", color = RingInnerFundamentals, style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        fundamentals?.fundamentalsScore?.let { it.toInt().toString() } ?: "—",
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(" pct vs peers", color = TextMuted, style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        if (signals.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp)
                    .clickable(onClick = onToggleSignals),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    if (signalsOpen) "HIDE ${signals.size} TECHNICAL SIGNALS" else "SHOW ${signals.size} TECHNICAL SIGNALS",
                    color = RingOuterTechnical,
                    style = MaterialTheme.typography.labelSmall,
                )
                Text(if (signalsOpen) "▲" else "▼", color = RingOuterTechnical)
            }
            if (signalsOpen) {
                signals.forEach { s ->
                    val color = when (signalPointsSign(s.points)) {
                        1 -> com.watchtower.app.ui.theme.UpColor
                        -1 -> com.watchtower.app.ui.theme.DownNeutral
                        else -> TextMuted
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().bottomHairline(HairlineFaint).padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(s.label, color = TextBody, modifier = Modifier.weight(1f))
                        Text(s.value, color = TextMuted, style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.width(10.dp))
                        Text(s.points, color = color)
                    }
                }
            }
        }
    }
}

@Composable
private fun FundamentalsSection(fundamentals: TickerDetail.Fundamentals?) {
    if (fundamentals == null) return
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
        Text("FUNDAMENTALS", color = TextMuted, style = MaterialTheme.typography.labelSmall)
        Spacer(Modifier.height(10.dp))
        listOf(
            "P/E ratio" to fundamentals.peRatio,
            "P/B ratio" to fundamentals.pbRatio,
            "Revenue growth YoY" to fundamentals.revenueGrowthYoy,
            "Profit margin" to fundamentals.profitMargin,
        ).forEach { (label, value) ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(label, color = TextBody)
                Text(value?.let { String.format("%.1f", it) } ?: "—", color = TextPrimary)
            }
        }
    }
}

@Composable
private fun EarningsSection(fundamentals: TickerDetail.Fundamentals?) {
    if (fundamentals?.forwardEpsEstimate == null) return
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .border(1.dp, HairlineOnDark)
            .padding(14.dp),
    ) {
        Text("FWD EPS ESTIMATE", color = TextMuted, style = MaterialTheme.typography.labelSmall)
        Spacer(Modifier.height(6.dp))
        Text(
            "$${String.format("%.2f", fundamentals.forwardEpsEstimate)}",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
        )
    }
}

@Composable
private fun NewsSection(news: List<NewsItem>) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
        Text("RELEVANT NEWS", color = TextMuted, style = MaterialTheme.typography.labelSmall)
        Spacer(Modifier.height(8.dp))
        if (news.isEmpty()) {
            Text(
                "No recent news flagged for this ticker.",
                color = TextMuted,
                style = MaterialTheme.typography.bodySmall,
            )
        } else {
            news.forEach { item ->
                Column(modifier = Modifier.fillMaxWidth().bottomHairline(HairlineFaint).padding(vertical = 10.dp)) {
                    Row {
                        item.source?.let { Text(it, color = RingOuterTechnical, style = MaterialTheme.typography.labelSmall) }
                        Spacer(Modifier.width(8.dp))
                        Text(formatRelativeTime(item.newsDatetime), color = TextMuted, style = MaterialTheme.typography.labelSmall)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(item.headline, color = TextBody)
                }
            }
        }
    }
}

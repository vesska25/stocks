package com.watchtower.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.watchtower.app.WatchtowerApplication
import com.watchtower.app.data.model.TickerSummary
import com.watchtower.app.ui.components.ScoreRing
import com.watchtower.app.ui.components.ScoreRingSizeMover
import com.watchtower.app.ui.components.ScoreRingSizeTable
import com.watchtower.app.ui.components.bottomHairline
import com.watchtower.app.ui.components.formatChangePct
import com.watchtower.app.ui.components.formatPrice
import com.watchtower.app.ui.components.formatRelativeTime
import com.watchtower.app.ui.components.orBlankIfLiteralNull
import com.watchtower.app.ui.theme.AccentTint06
import com.watchtower.app.ui.theme.HairlineOnDark
import com.watchtower.app.ui.theme.RingOuterTechnical
import com.watchtower.app.ui.theme.ScreenBg
import com.watchtower.app.ui.theme.TextBody
import com.watchtower.app.ui.theme.TextMuted
import com.watchtower.app.ui.theme.TextPrimary
import com.watchtower.app.ui.theme.arrowColor
import com.watchtower.app.ui.theme.arrowGlyph
import com.watchtower.app.ui.theme.directionColor

@Composable
fun HomeScreen(
    onOpenTicker: (String) -> Unit,
    onOpenDigests: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    val app = LocalContext.current.applicationContext as WatchtowerApplication
    val factory = viewModelFactory { initializer { HomeViewModel(app.repository) } }
    val viewModel: HomeViewModel = viewModel(factory = factory)
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().background(ScreenBg)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text("WATCHTOWER", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
                Text(
                    "${state.tickers.size} tickers",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                )
            }
            IconButton(onClick = onOpenSettings) {
                Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = RingOuterTechnical)
            }
        }

        when {
            state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = RingOuterTechnical)
            }
            state.error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Couldn't load tickers: ${state.error}", color = TextMuted)
                    Spacer(Modifier.height(8.dp))
                    Text("Tap to retry", color = RingOuterTechnical, modifier = Modifier.clickable { viewModel.refresh() })
                }
            }
            else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                item { MoversStrip(state.movers, onOpenTicker) }
                item {
                    state.latestDigest?.let { digest ->
                        DigestPreviewCard(digestText = digest.digestText, createdAt = digest.createdAt, onOpenDigests = onOpenDigests)
                    }
                }
                item { SectionHeader("WATCHLIST", state.rows.size, state.tickers.size) }
                item { IndustryFilterRow(state.industries, state.filterIndustry, viewModel::setFilter) }
                item { WatchlistHeaderRow(state.sortKey, state.sortAscending, viewModel::setSort) }
                items(state.rows, key = { it.ticker }) { row ->
                    WatchlistRow(row, onClick = { onOpenTicker(row.ticker) })
                }
            }
        }
    }
}

@Composable
private fun MoversStrip(movers: List<TickerSummary>, onOpenTicker: (String) -> Unit) {
    Column {
        Text(
            "BIGGEST MOVERS",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp),
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(movers, key = { it.ticker }) { m ->
                Column(
                    modifier = Modifier
                        .width(146.dp)
                        .border(1.dp, HairlineOnDark)
                        .clickable { onOpenTicker(m.ticker) }
                        .padding(12.dp),
                ) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Column {
                            Text(m.ticker, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                            Text(formatPrice(m.price), style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        }
                        ScoreRing(m.compositeScore, m.fundamentalsScore, size = ScoreRingSizeMover)
                    }
                    Spacer(Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(arrowGlyph(m.changePct), color = arrowColor(m.changePct))
                        Text(formatChangePct(m.changePct), color = directionColor(m.changePct), fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun DigestPreviewCard(digestText: String, createdAt: String?, onOpenDigests: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(1.dp, com.watchtower.app.ui.theme.AccentBorder)
            .background(AccentTint06)
            .clickable { onOpenDigests() }
            .padding(14.dp),
    ) {
        Text("TODAY'S DIGEST · ${formatRelativeTime(createdAt)}", style = MaterialTheme.typography.labelSmall, color = RingOuterTechnical)
        Spacer(Modifier.height(8.dp))
        Text(digestText, color = TextBody, style = MaterialTheme.typography.bodyMedium, maxLines = 3, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.height(10.dp))
        Text("ALL DIGESTS →", style = MaterialTheme.typography.labelSmall, color = RingOuterTechnical)
    }
}

@Composable
private fun SectionHeader(title: String, shown: Int, total: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(title, style = MaterialTheme.typography.labelSmall, color = TextMuted)
        Text("$shown OF $total", style = MaterialTheme.typography.labelSmall, color = TextMuted)
    }
}

@Composable
private fun IndustryFilterRow(industries: List<String>, selected: String?, onSelect: (String?) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        item { FilterChip("ALL", selected == null) { onSelect(null) } }
        items(industries) { industry ->
            FilterChip(industry, selected == industry) { onSelect(industry) }
        }
    }
}

@Composable
private fun FilterChip(label: String, active: Boolean, onClick: () -> Unit) {
    val border = if (active) RingOuterTechnical else HairlineOnDark
    val textColor = if (active) TextPrimary else TextMuted
    Text(
        label,
        color = textColor,
        style = MaterialTheme.typography.labelSmall,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
            .widthIn(max = 140.dp)
            .border(1.dp, border)
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp),
    )
}

@Composable
private fun WatchlistHeaderRow(sortKey: SortKey, ascending: Boolean, onSort: (SortKey) -> Unit) {
    fun arrow(key: SortKey) = if (sortKey == key) (if (ascending) "↑" else "↓") else ""
    fun color(key: SortKey) = if (sortKey == key) RingOuterTechnical else TextMuted

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .bottomHairline(HairlineOnDark)
            .padding(horizontal = 16.dp, vertical = 6.dp),
    ) {
        Text(
            "TICKER ${arrow(SortKey.SYMBOL)}",
            color = color(SortKey.SYMBOL),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.weight(1f).clickable { onSort(SortKey.SYMBOL) },
        )
        Text(
            "PRICE ${arrow(SortKey.PRICE)}",
            color = color(SortKey.PRICE),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.width(74.dp).clickable { onSort(SortKey.PRICE) },
        )
        Text(
            "%CHG ${arrow(SortKey.CHANGE)}",
            color = color(SortKey.CHANGE),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.width(62.dp).clickable { onSort(SortKey.CHANGE) },
        )
        Text(
            "T/F ${arrow(SortKey.SCORE)}",
            color = color(SortKey.SCORE),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.width(38.dp).clickable { onSort(SortKey.SCORE) },
        )
    }
}

@Composable
private fun WatchlistRow(row: TickerSummary, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .bottomHairline(HairlineOnDark.copy(alpha = 0.55f))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(row.ticker, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                row.industry.orBlankIfLiteralNull()?.let {
                    Spacer(Modifier.width(6.dp))
                    Text(
                        it,
                        color = RingOuterTechnical,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.border(1.dp, com.watchtower.app.ui.theme.AccentBorder).padding(horizontal = 4.dp, vertical = 2.dp),
                    )
                }
            }
            row.name.orBlankIfLiteralNull()?.let {
                Text(it, color = TextMuted, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        Text(formatPrice(row.price), color = TextPrimary, modifier = Modifier.width(74.dp))
        Row(modifier = Modifier.width(62.dp)) {
            Text(arrowGlyph(row.changePct), color = arrowColor(row.changePct))
            Text(formatChangePct(row.changePct), color = directionColor(row.changePct))
        }
        Box(modifier = Modifier.width(38.dp), contentAlignment = Alignment.CenterEnd) {
            ScoreRing(row.compositeScore, row.fundamentalsScore, size = ScoreRingSizeTable)
        }
    }
}

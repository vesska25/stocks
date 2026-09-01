package com.watchtower.app.ui.digest

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.watchtower.app.WatchtowerApplication
import com.watchtower.app.data.model.DigestSummary
import com.watchtower.app.ui.components.bottomHairline
import com.watchtower.app.ui.components.formatPrice
import com.watchtower.app.ui.components.formatRelativeTime
import com.watchtower.app.ui.theme.HairlineFaint
import com.watchtower.app.ui.theme.RingOuterTechnical
import com.watchtower.app.ui.theme.ScreenBg
import com.watchtower.app.ui.theme.TextBody
import com.watchtower.app.ui.theme.TextMuted
import com.watchtower.app.ui.theme.TextPrimary

@Composable
fun DigestHistoryScreen(onBack: () -> Unit, onOpenTicker: (String) -> Unit) {
    val app = LocalContext.current.applicationContext as WatchtowerApplication
    val factory = viewModelFactory { initializer { DigestViewModel(app.repository) } }
    val viewModel: DigestViewModel = viewModel(factory = factory)
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().background(ScreenBg)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = RingOuterTechnical)
            }
            Text("DIGEST HISTORY", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(state.items, key = { it.id }) { digest ->
                DigestRow(
                    digest = digest,
                    expanded = digest.id in state.expandedIds,
                    onToggle = { viewModel.toggleExpanded(digest.id) },
                    onOpenTicker = onOpenTicker,
                )
            }
            item {
                if (state.hasMore) {
                    LaunchedEffect(state.items.size) { viewModel.loadNextPage() }
                    Text(
                        "Loading…",
                        color = TextMuted,
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                    )
                } else if (state.error != null) {
                    Text(
                        "Couldn't load more: ${state.error}",
                        color = TextMuted,
                        modifier = Modifier.fillMaxWidth().padding(16.dp).clickable { viewModel.loadNextPage() },
                    )
                }
            }
        }
    }
}

@Composable
private fun DigestRow(
    digest: DigestSummary,
    expanded: Boolean,
    onToggle: () -> Unit,
    onOpenTicker: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .bottomHairline(HairlineFaint)
            .clickable(onClick = onToggle)
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "${digest.tickerSnapshots.size.takeIf { it > 0 } ?: digest.tickers.size} FLAGGED",
                color = RingOuterTechnical,
                style = MaterialTheme.typography.labelSmall,
            )
            Spacer(Modifier.size(8.dp))
            Text(formatRelativeTime(digest.createdAt), color = TextMuted, style = MaterialTheme.typography.labelSmall)
        }
        Spacer(Modifier.height(6.dp))
        Text(digest.digestText, color = TextBody, style = MaterialTheme.typography.bodyMedium)

        if (expanded) {
            Spacer(Modifier.height(10.dp))
            digest.tickerSnapshots.forEach { snap ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenTicker(snap.ticker) }
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(snap.ticker, color = TextPrimary, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                    Text(
                        "T${snap.compositeScore?.let { (if (it >= 0) "+" else "") + it } ?: "—"}" +
                            "/F${snap.fundamentalsScore?.let { (if (it >= 0) "+" else "") + it } ?: "—"}",
                        color = TextMuted,
                        style = MaterialTheme.typography.labelSmall,
                    )
                    Spacer(Modifier.size(10.dp))
                    Text(formatPrice(snap.realtimePrice), color = TextPrimary)
                }
            }
            if (digest.tickerSnapshots.isEmpty() && digest.tickers.isNotEmpty()) {
                Text(
                    digest.tickers.joinToString(", "),
                    color = TextMuted,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

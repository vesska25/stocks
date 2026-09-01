package com.watchtower.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.watchtower.app.data.model.PricePoint
import com.watchtower.app.ui.theme.HairlineFaint
import com.watchtower.app.ui.theme.RingOuterTechnical
import com.watchtower.app.ui.theme.TextPrimary

/**
 * One steel line + filled area beneath, no axes/candles — matches the
 * design's chart style. `sma` (same length as `points` when non-empty) is a
 * real 50-day close SMA computed client-side from historical_prices — see
 * DetailUiState.sma50 — drawn as a dashed overlay, matching the design.
 */
@Composable
fun Sparkline(points: List<PricePoint>, sma: List<Double?> = emptyList(), modifier: Modifier = Modifier) {
    val closes = points.mapNotNull { it.close }
    Canvas(modifier = modifier.fillMaxWidth().height(132.dp)) {
        if (closes.size < 2) return@Canvas
        val smaValues = sma.filterNotNull()
        val lo = (closes + smaValues).min()
        val hi = (closes + smaValues).max()
        val span = (hi - lo).takeIf { it > 0.0 } ?: 1.0

        fun x(i: Int) = (i.toFloat() / (closes.size - 1)) * size.width
        fun y(v: Double) = size.height - ((v - lo) / span * size.height).toFloat()

        val linePath = Path().apply {
            closes.forEachIndexed { i, v ->
                if (i == 0) moveTo(x(i), y(v)) else lineTo(x(i), y(v))
            }
        }
        val areaPath = Path().apply {
            addPath(linePath)
            lineTo(x(closes.size - 1), size.height)
            lineTo(x(0), size.height)
            close()
        }

        drawLine(HairlineFaint, Offset(0f, 1f), Offset(size.width, 1f))
        drawLine(HairlineFaint, Offset(0f, size.height / 2f), Offset(size.width, size.height / 2f))
        drawLine(HairlineFaint, Offset(0f, size.height - 1f), Offset(size.width, size.height - 1f))

        drawPath(areaPath, color = RingOuterTechnical.copy(alpha = 0.12f), style = Fill)
        drawPath(linePath, color = RingOuterTechnical, style = Stroke(width = 4f))

        if (sma.size == closes.size) {
            val smaPath = Path()
            var drawing = false
            for (i in sma.indices) {
                val v = sma[i] ?: run { drawing = false; continue }
                if (!drawing) {
                    smaPath.moveTo(x(i), y(v))
                    drawing = true
                } else {
                    smaPath.lineTo(x(i), y(v))
                }
            }
            drawPath(
                smaPath,
                color = TextPrimary.copy(alpha = 0.45f),
                style = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f))),
            )
        }
    }
}

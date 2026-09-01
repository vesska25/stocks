package com.watchtower.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.watchtower.app.ui.theme.HairlineOnDark
import com.watchtower.app.ui.theme.RingInnerFundamentals
import com.watchtower.app.ui.theme.RingOuterTechnical

/**
 * Concentric dual ring: outer = technical composite (-3..+7, origin at -3),
 * inner = fundamentals score vs same-industry peers (-4..+4 — the sum of
 * four +1/0/-1 peer-comparison signals: P/E, P/B, margin, growth; confirmed
 * against production data, not the 0-100 percentile originally assumed from
 * the design brief). Same proportions at every size, per the design's "same
 * glyph at 3 sizes" rule (26dp table rows, 34dp mover cards, 92dp detail).
 *
 * A 270° sweep starting at 135° (not a full circle) — the 90° gap at the
 * bottom-left reads as the scale's empty origin, matching the source design.
 */
@Composable
fun ScoreRing(
    technicalScore: Double?,
    fundamentalsScore: Double?,
    size: Dp,
    modifier: Modifier = Modifier,
) {
    val techFraction = technicalScore?.let { ((it + 3.0) / 10.0).coerceIn(0.0, 1.0) } ?: 0.0
    val fundFraction = fundamentalsScore?.let { ((it + 4.0) / 8.0).coerceIn(0.0, 1.0) } ?: 0.0

    Canvas(modifier = modifier.size(size)) {
        val strokeWidth = this.size.minDimension * 0.17f
        val outerRadius = this.size.minDimension * 0.875f / 2f
        val innerRadius = this.size.minDimension * 0.58f / 2f
        val center = Offset(this.size.width / 2f, this.size.height / 2f)

        fun ring(radius: Float, fraction: Double, color: Color) {
            val diameter = radius * 2f
            val topLeft = Offset(center.x - radius, center.y - radius)
            val arcSize = Size(diameter, diameter)
            drawArc(
                color = HairlineOnDark,
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth),
            )
            drawArc(
                color = color,
                startAngle = 135f,
                sweepAngle = 270f * fraction.toFloat(),
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth),
            )
        }

        ring(outerRadius, techFraction, RingOuterTechnical)
        ring(innerRadius, fundFraction, RingInnerFundamentals)
    }
}

val ScoreRingSizeTable = 26.dp
val ScoreRingSizeMover = 34.dp
val ScoreRingSizeDetail = 92.dp

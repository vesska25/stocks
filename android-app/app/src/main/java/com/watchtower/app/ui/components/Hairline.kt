package com.watchtower.app.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

/** A 1px bottom rule — the design's row/section separator style, used in place of full borders. */
fun Modifier.bottomHairline(color: Color): Modifier = drawBehind {
    drawLine(
        color = color,
        start = Offset(0f, size.height - 1f),
        end = Offset(size.width, size.height - 1f),
        strokeWidth = 1f,
    )
}

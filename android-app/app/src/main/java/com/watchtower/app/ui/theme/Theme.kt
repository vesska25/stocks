package com.watchtower.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// Dark-only by design brief ("dark trading aesthetic"), regardless of system theme.
private val WatchtowerColorScheme = darkColorScheme(
    background = ScreenBg,
    surface = ScreenBg,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    primary = RingOuterTechnical,
    secondary = RingInnerFundamentals,
    error = DownRed,
)

@Composable
fun WatchtowerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = WatchtowerColorScheme,
        typography = Typography,
        content = content,
    )
}

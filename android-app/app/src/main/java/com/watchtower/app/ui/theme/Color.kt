package com.watchtower.app.ui.theme

import androidx.compose.ui.graphics.Color

// Design tokens from the Claude Design handoff (Industry design system,
// inverted to a dark trading ground). Kept as named constants rather than
// Material role colors because the design's contrast rules (gain/loss via
// glyph+contrast, not hue) don't map cleanly onto Material's semantic roles.

val ScreenBg = Color(0xFF101315)
val TextPrimary = Color(0xFFF2F2F3)
val TextBody = Color(0xFFDFE3E6)
val TextMuted = Color(0xFF8B9095)

val RingOuterTechnical = Color(0xFF94BCE3)
val RingInnerFundamentals = Color(0xFF4D8FD6)

val UpColor = Color(0xFFB5D9FD)
val DownNeutral = Color(0xFFB7B7BA)
val DownRed = Color(0xFFC2503F) // the one deliberate non-steel color, per the design notes

val HairlineOnDark = Color(0x24F2F2F3) // rgba(242,242,243,.14)
val HairlineFaint = Color(0x14F2F2F3)  // rgba(242,242,243,.08)
val AccentBorder = Color(0x595980A6)   // rgba(89,128,166,.35) border tint
val AccentTint06 = Color(0x0F94BCE3)   // rgba(148,188,227,.06)
val AccentTint11 = Color(0x1C94BCE3)   // rgba(148,188,227,.11)

fun directionColor(changePct: Double?): Color = when {
    changePct == null -> TextMuted
    changePct >= 0 -> UpColor
    else -> DownNeutral
}

fun arrowColor(changePct: Double?): Color = when {
    changePct == null -> TextMuted
    changePct >= 0 -> UpColor
    else -> DownRed
}

fun arrowGlyph(changePct: Double?): String = when {
    changePct == null -> ""
    changePct >= 0 -> "▲"
    else -> "▼"
}

package com.watchtower.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// The design specifies Barlow / Barlow Condensed / IBM Plex Mono. Those
// aren't bundled here (no network access to fetch font files in this
// environment) — system sans-serif and monospace stand in. Swap FontFamily
// values below for the real fonts once added under res/font.
val BodyFont = FontFamily.SansSerif
val CondensedFont = FontFamily.SansSerif
val MonoFont = FontFamily.Monospace

val Typography = Typography(
    bodyLarge = TextStyle(fontFamily = BodyFont, fontWeight = FontWeight.Normal, fontSize = 14.sp),
    bodyMedium = TextStyle(fontFamily = BodyFont, fontWeight = FontWeight.Normal, fontSize = 13.sp),
    bodySmall = TextStyle(fontFamily = BodyFont, fontWeight = FontWeight.Normal, fontSize = 11.sp),
    titleLarge = TextStyle(fontFamily = CondensedFont, fontWeight = FontWeight.SemiBold, fontSize = 21.sp),
    titleMedium = TextStyle(fontFamily = CondensedFont, fontWeight = FontWeight.SemiBold, fontSize = 17.sp),
    labelSmall = TextStyle(fontFamily = MonoFont, fontWeight = FontWeight.Medium, fontSize = 10.sp, letterSpacing = 0.12.sp),
)

val NumericStyle = TextStyle(fontFamily = MonoFont, fontWeight = FontWeight.Medium)

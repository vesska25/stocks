package com.watchtower.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.watchtower.app.R

// The design specifies Barlow / Barlow Condensed / IBM Plex Mono, bundled
// under res/font (OFL-licensed, from the google/fonts repo).
val BodyFont = FontFamily(
    Font(R.font.barlow_regular, FontWeight.Normal),
    Font(R.font.barlow_medium, FontWeight.Medium),
    Font(R.font.barlow_semibold, FontWeight.SemiBold),
)
val CondensedFont = FontFamily(
    Font(R.font.barlow_condensed_regular, FontWeight.Normal),
    Font(R.font.barlow_condensed_medium, FontWeight.Medium),
    Font(R.font.barlow_condensed_semibold, FontWeight.SemiBold),
)
val MonoFont = FontFamily(
    Font(R.font.ibm_plex_mono_regular, FontWeight.Normal),
    Font(R.font.ibm_plex_mono_medium, FontWeight.Medium),
)

val Typography = Typography(
    bodyLarge = TextStyle(fontFamily = BodyFont, fontWeight = FontWeight.Normal, fontSize = 14.sp),
    bodyMedium = TextStyle(fontFamily = BodyFont, fontWeight = FontWeight.Normal, fontSize = 13.sp),
    bodySmall = TextStyle(fontFamily = BodyFont, fontWeight = FontWeight.Normal, fontSize = 11.sp),
    titleLarge = TextStyle(fontFamily = CondensedFont, fontWeight = FontWeight.SemiBold, fontSize = 21.sp),
    titleMedium = TextStyle(fontFamily = CondensedFont, fontWeight = FontWeight.SemiBold, fontSize = 17.sp),
    labelSmall = TextStyle(fontFamily = MonoFont, fontWeight = FontWeight.Medium, fontSize = 10.sp, letterSpacing = 0.12.sp),
)

val NumericStyle = TextStyle(fontFamily = MonoFont, fontWeight = FontWeight.Medium)

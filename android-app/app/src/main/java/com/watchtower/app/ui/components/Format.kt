package com.watchtower.app.ui.components

import java.time.Instant
import java.time.OffsetDateTime
import java.time.format.DateTimeParseException
import java.util.Locale
import kotlin.math.abs

/**
 * Some rows in the source DB have the literal string "null" stored instead
 * of a real SQL NULL (a pipeline data-quality issue, seen for a couple of
 * tickers in production) — treat it the same as missing rather than
 * rendering the word "null" on screen.
 */
fun String?.orBlankIfLiteralNull(): String? =
    this?.takeUnless { it.equals("null", ignoreCase = true) }

fun formatPrice(price: Double?): String =
    price?.let { String.format(Locale.US, "%.2f", it) } ?: "—"

fun formatChangePct(changePct: Double?): String =
    changePct?.let { String.format(Locale.US, "%.2f%%", abs(it)) } ?: "—"

fun formatScoreInt(score: Double?): String =
    score?.let { (if (it >= 0) "+" else "") + it.toInt().toString() } ?: "—"

/** Best-effort "Xm ago" / "Xh ago" from an ISO-8601 instant string. Falls back to the raw string. */
fun formatRelativeTime(isoInstant: String?): String {
    if (isoInstant.isNullOrBlank()) return "—"
    val instant = try {
        Instant.parse(isoInstant)
    } catch (e: DateTimeParseException) {
        try {
            OffsetDateTime.parse(isoInstant).toInstant()
        } catch (e2: DateTimeParseException) {
            return isoInstant
        }
    }
    val minutes = java.time.Duration.between(instant, Instant.now()).toMinutes()
    return when {
        minutes < 1 -> "just now"
        minutes < 60 -> "${minutes}m ago"
        minutes < 60 * 24 -> "${minutes / 60}h ago"
        else -> "${minutes / (60 * 24)}d ago"
    }
}

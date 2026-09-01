package com.watchtower.app.ui.detail

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

data class SignalEntry(val label: String, val value: String, val points: String)

/**
 * analytics_results.signals is a JSONB column shaped by the n8n pipeline,
 * not fixed by this API — parsed defensively against the {label,value,pts}
 * shape the original design brief describes; anything that doesn't fit is
 * just dropped rather than crashing the screen.
 */
fun parseSignals(element: JsonElement?): List<SignalEntry> {
    if (element == null) return emptyList()
    return runCatching {
        element.jsonArray.mapNotNull { item ->
            val obj = item as? JsonObject ?: return@mapNotNull null
            val label = obj["label"]?.jsonPrimitive?.contentOrNull ?: return@mapNotNull null
            val value = obj["value"]?.jsonPrimitive?.contentOrNull ?: ""
            val pts = obj["pts"]?.jsonPrimitive?.contentOrNull ?: ""
            SignalEntry(label, value, pts)
        }
    }.getOrDefault(emptyList())
}

fun signalPointsSign(points: String): Int = when {
    points.startsWith("+") -> 1
    points.startsWith("-") -> -1
    else -> 0
}

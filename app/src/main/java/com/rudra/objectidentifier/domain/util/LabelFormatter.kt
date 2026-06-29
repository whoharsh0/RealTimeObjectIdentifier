package com.rudra.objectidentifier.domain.util

object LabelFormatter {

    private val synonyms = mapOf(
        "cell phone" to "Phone",
        "mobile phone" to "Phone",
        "tv" to "TV",
        "television" to "TV",
        "laptop" to "Laptop",
        "notebook" to "Laptop",
        "remote" to "Remote",
        "dining table" to "Table",
        "potted plant" to "Plant",
        "traffic light" to "Traffic light",
        "stop sign" to "Stop sign",
        "fire hydrant" to "Hydrant",
        "sports ball" to "Ball",
        "wine glass" to "Wine glass",
        "handbag" to "Bag",
        "backpack" to "Backpack"
    )

    fun format(rawLabel: String): String {
        val normalized = rawLabel.trim().lowercase()
        if (normalized.isBlank()) return rawLabel
        return synonyms[normalized] ?: rawLabel.trim()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }

    fun matchesFilter(label: String, filter: String): Boolean {
        if (filter.isBlank()) return true
        return label.contains(filter, ignoreCase = true) ||
            format(label).contains(filter, ignoreCase = true)
    }

    fun sceneDescription(labels: List<String>, maxItems: Int = 4): String {
        if (labels.isEmpty()) return "No objects detected yet"
        val unique = labels.map(::format).distinct().take(maxItems)
        return when (unique.size) {
            1 -> "Scene: ${unique.first()}"
            2 -> "Scene: ${unique[0]} and ${unique[1]}"
            else -> "Scene: ${unique.dropLast(1).joinToString(", ")}, and ${unique.last()}"
        }
    }
}

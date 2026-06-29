package com.rudra.objectidentifier.domain.util

object ObjectCategory {

    private val people = setOf("person")
    private val vehicles = setOf("car", "bus", "truck", "motorcycle", "bicycle", "train", "boat", "airplane")
    private val animals = setOf("dog", "cat", "bird", "horse", "sheep", "cow", "elephant", "bear", "zebra", "giraffe")
    private val food = setOf(
        "banana", "apple", "orange", "broccoli", "carrot", "hot dog", "pizza", "donut", "cake", "sandwich"
    )
    private val furniture = setOf("chair", "couch", "bed", "dining table", "toilet", "tv")

    fun colorArgbForLabel(label: String, highContrast: Boolean = false): Int {
        val key = label.trim().lowercase()
        val color = when {
            key in people -> 0xFF4FC3F7.toInt()
            key in vehicles -> 0xFFFFB74D.toInt()
            key in animals -> 0xFF81C784.toInt()
            key in food -> 0xFFFF8A65.toInt()
            key in furniture -> 0xFFBA68C8.toInt()
            else -> 0xFF26C6DA.toInt()
        }
        return if (highContrast) color or 0xFF000000.toInt() else color
    }
}

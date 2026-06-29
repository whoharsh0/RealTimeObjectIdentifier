package com.rudra.objectidentifier.domain.model

data class ScanHistoryEntry(
    val id: Long,
    val timestampMillis: Long,
    val topLabels: String,
    val detectionCount: Int,
    val scanMode: ScanMode
)

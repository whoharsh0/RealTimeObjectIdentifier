package com.rudra.objectidentifier.domain.model

enum class CameraLens {
    BACK,
    FRONT;

    fun toggled(): CameraLens = if (this == BACK) FRONT else BACK
}

package com.rudra.objectidentifier.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class CameraLensTest {

    @Test
    fun toggled_switchesBetweenBackAndFront() {
        assertEquals(CameraLens.FRONT, CameraLens.BACK.toggled())
        assertEquals(CameraLens.BACK, CameraLens.FRONT.toggled())
    }
}

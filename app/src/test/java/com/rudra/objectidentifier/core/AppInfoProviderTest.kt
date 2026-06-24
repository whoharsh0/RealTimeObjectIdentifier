package com.rudra.objectidentifier.core

import org.junit.Assert.assertEquals
import org.junit.Test

class AppInfoProviderTest {

    private val provider = AppInfoProvider()

    @Test
    fun getAppTitle_returnsExpectedName() {
        assertEquals("Real-Time Object Identifier", provider.getAppTitle())
    }

    @Test
    fun getVersionName_isNotBlank() {
        assert(provider.getVersionName().isNotBlank())
    }
}

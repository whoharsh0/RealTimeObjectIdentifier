package com.rudra.objectidentifier.di

import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertSame
import org.junit.Test

class DispatcherModuleTest {

    @Test
    fun provideIoDispatcher_returnsDispatchersIo() {
        assertSame(Dispatchers.IO, DispatcherModule.provideIoDispatcher())
    }

    @Test
    fun provideMainDispatcher_returnsDispatchersMain() {
        assertSame(Dispatchers.Main, DispatcherModule.provideMainDispatcher())
    }

    @Test
    fun provideDefaultDispatcher_returnsDispatchersDefault() {
        assertSame(Dispatchers.Default, DispatcherModule.provideDefaultDispatcher())
    }
}

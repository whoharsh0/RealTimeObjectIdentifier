package com.rudra.objectidentifier

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rudra.objectidentifier.core.AppInfoProvider
import com.rudra.objectidentifier.di.DefaultDispatcher
import com.rudra.objectidentifier.di.IoDispatcher
import com.rudra.objectidentifier.di.MainDispatcher
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class HiltInjectionTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var appInfoProvider: AppInfoProvider

    @Inject
    @IoDispatcher
    lateinit var ioDispatcher: CoroutineDispatcher

    @Inject
    @MainDispatcher
    lateinit var mainDispatcher: CoroutineDispatcher

    @Inject
    @DefaultDispatcher
    lateinit var defaultDispatcher: CoroutineDispatcher

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun appInfoProvider_isInjectedWithCorrectTitle() {
        assertEquals("Real-Time Object Identifier", appInfoProvider.getAppTitle())
        assertEquals("1.0.0", appInfoProvider.getVersionName())
    }

    @Test
    fun dispatchers_areInjectedWithCorrectInstances() {
        assertNotNull(ioDispatcher)
        assertNotNull(mainDispatcher)
        assertNotNull(defaultDispatcher)
        assertSame(Dispatchers.IO, ioDispatcher)
        assertSame(Dispatchers.Main, mainDispatcher)
        assertSame(Dispatchers.Default, defaultDispatcher)
    }

    @Test
    fun appInfoProvider_isSingletonAcrossInjections() {
        val secondProvider = appInfoProvider
        hiltRule.inject()
        assertSame(secondProvider, appInfoProvider)
    }
}

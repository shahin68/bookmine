package com.shahin.core.common.di

import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class DispatchersModuleTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    @DefaultDispatcher
    lateinit var defaultDispatcher: CoroutineDispatcher

    @Inject
    @IoDispatcher
    lateinit var ioDispatcher: CoroutineDispatcher

    @Inject
    @MainDispatcher
    lateinit var mainDispatcher: CoroutineDispatcher

    @Test
    fun testDefaultDispatcher() {
        hiltRule.inject()
        assertEquals(Dispatchers.Default, defaultDispatcher)
    }

    @Test
    fun testIoDispatcher() {
        hiltRule.inject()
        assertEquals(Dispatchers.IO, ioDispatcher)
    }

    @Test
    fun testMainDispatcher() {
        hiltRule.inject()
        assertEquals(Dispatchers.Main, mainDispatcher)
    }
}
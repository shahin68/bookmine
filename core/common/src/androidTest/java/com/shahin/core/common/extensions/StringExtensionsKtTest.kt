package com.shahin.core.common.extensions

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class StringExtensionsTest {

    @Test
    fun testHash() {
        val string = "dcb2d450-55c1-413b-8448-2fe7d6911929"
        val hashedString = string.hash()

        assertNotEquals(string, hashedString)

        val realHashedString = "bNrLEmmfOM+rvV6f4ZQSktn6JQrYWnpdnX6VtUttSWA="
        assertEquals(hashedString, realHashedString)
    }
}
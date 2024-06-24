package com.shahin.core.common.extensions

import com.shahin.core.common.commons.Constants.BOOK_MINE_DATE_PATTERN
import com.shahin.core.common.commons.Constants.MOCKEY_DATE_PATTERN
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

    @Test
    fun testDatePatterns() {
        val date = "10/18/1851"
        val expectedDateValue = "Fri, Jan 10, '51"

        val formattedDate = date.formatReleaseDate(
            inputPattern = MOCKEY_DATE_PATTERN,
            outputPattern = BOOK_MINE_DATE_PATTERN
        )
        assertEquals(formattedDate, expectedDateValue)
    }

    @Test
    fun testInvalidDatePatterns() {
        val date = "800 BC"
        val expectedDateValue = "800 BC"

        val formattedDate = date.formatReleaseDate(
            inputPattern = MOCKEY_DATE_PATTERN,
            outputPattern = BOOK_MINE_DATE_PATTERN
        )
        assertEquals(formattedDate, expectedDateValue)
    }

    @Test
    fun testAnotherInvalidDatePatterns() {
        val date = "1320"
        val expectedDateValue = "1320"

        val formattedDate = date.formatReleaseDate(
            inputPattern = MOCKEY_DATE_PATTERN,
            outputPattern = BOOK_MINE_DATE_PATTERN
        )
        assertEquals(formattedDate, expectedDateValue)
    }
}
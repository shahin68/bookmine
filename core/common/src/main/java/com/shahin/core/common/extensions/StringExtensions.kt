package com.shahin.core.common.extensions

import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

fun String.hash(): String {
    val messageDigest = MessageDigest.getInstance("SHA-256")
    val hashBytes = messageDigest.digest(toByteArray(StandardCharsets.UTF_8))
    return Base64.encodeToString(hashBytes, Base64.NO_WRAP)
}

fun String.formatReleaseDate(inputPattern: String, outputPattern: String): String {
    if (isBlank()) return this
    return getFormattedDate(
        dateString = this,
        inputPattern = inputPattern,
        outputPattern = outputPattern
    )
}

private fun getFormattedDate(
    dateString: String,
    inputPattern: String,
    outputPattern: String,
): String {
    val inputFormatter = SimpleDateFormat(inputPattern, Locale.US)
    inputFormatter.timeZone = TimeZone.getTimeZone("UTC")
    try {
        val parsedDate = inputFormatter.parse(dateString)
        if (parsedDate != null) {
            val outputFormatter = SimpleDateFormat(outputPattern, Locale.US)
            outputFormatter.timeZone = TimeZone.getTimeZone("UTC")
            return outputFormatter.format(parsedDate)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return dateString
}
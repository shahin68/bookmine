package com.shahin.core.common.extensions

import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

fun String.hash(): String {
    val messageDigest = MessageDigest.getInstance("SHA-256")
    val hashBytes = messageDigest.digest(toByteArray(StandardCharsets.UTF_8))
    return Base64.encodeToString(hashBytes, Base64.NO_WRAP)
}
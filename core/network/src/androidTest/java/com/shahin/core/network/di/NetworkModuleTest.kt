package com.shahin.core.network.di

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SdkSuppress
import com.shahin.core.network.R
import com.shahin.core.network.test.BuildConfig
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.tls.HandshakeCertificates
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class NetworkModuleTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var okHttpClient: OkHttpClient

    @Inject
    lateinit var httpLoggingInterceptor: HttpLoggingInterceptor

    @Inject
    lateinit var httpInterceptor: Interceptor

    @Inject
    lateinit var certificates: HandshakeCertificates

    private lateinit var context: Context


    @Before
    fun setUp() {
        hiltRule.inject()
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun testOkHttpClientInjection() {
        assertNotNull(okHttpClient)
        assertNotNull(httpLoggingInterceptor)
        assertNotNull(httpInterceptor)
        assertNotNull(certificates)
    }

    @Test
    fun testProvidingOkHttpClient() {
        assertNotNull(okHttpClient)
        assertEquals(30000, okHttpClient.writeTimeoutMillis.toLong())
        assertEquals(30000, okHttpClient.readTimeoutMillis.toLong())

        val interceptors = okHttpClient.interceptors
        assertTrue(interceptors.contains(httpLoggingInterceptor))
        assertTrue(interceptors.contains(httpInterceptor))
    }

    @Test
    fun testHttpLoggingInterceptor() {
        if (BuildConfig.DEBUG) {
            assertEquals(httpLoggingInterceptor.level, HttpLoggingInterceptor.Level.BODY)
        } else {
            assertEquals(httpLoggingInterceptor.level, HttpLoggingInterceptor.Level.NONE)
        }
    }

    @Test
    @SdkSuppress(maxSdkVersion = Build.VERSION_CODES.M)
    fun testTrustedSSLCertificatesApiBelow24() {
        val certificateIds = listOf(R.raw.mocky_ca)
        val networkModule = NetworkModule()
        val handshakeCertificates = networkModule.provideTrustedSSLCertificatesApi23(context, certificateIds)

        assertNotNull(handshakeCertificates)
        assert(handshakeCertificates.trustManager.acceptedIssuers.isNotEmpty())
    }

    @Test
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.N)
    fun testTrustedSSLCertificatesApi24AndAbove() {
        val certificateIds = listOf(R.raw.mocky_ca)
        val networkModule = NetworkModule()
        val handshakeCertificates = networkModule.provideTrustedSSLCertificatesApi23(context, certificateIds)

        assertNotNull(handshakeCertificates)
        assert(handshakeCertificates.trustManager.acceptedIssuers.isEmpty())
    }

    @Test
    @SdkSuppress(maxSdkVersion = Build.VERSION_CODES.M)
    fun testLoadCertificateApiBelow24() {
        val networkModule = NetworkModule()
        val certificate = networkModule.loadCertificate(context, R.raw.mocky_ca)

        assertNotNull(certificate)
    }

    @Test
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.N)
    fun testLoadCertificateApi24AndAbove() {
        val networkModule = NetworkModule()
        val certificate = networkModule.loadCertificate(context, R.raw.mocky_ca)

        assertNull(certificate)
    }
}
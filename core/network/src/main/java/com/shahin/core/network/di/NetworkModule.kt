package com.shahin.core.network.di

import android.content.Context
import android.content.res.Resources.NotFoundException
import android.os.Build
import androidx.annotation.RawRes
import com.shahin.core.network.BuildConfig
import com.shahin.core.network.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.tls.HandshakeCertificates
import java.io.IOException
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * References to Trust certificates:
 *
 * [CustomTrust.kt](https://github.com/square/okhttp/blob/master/samples/guide/src/main/java/okhttp3/recipes/kt/CustomTrust.kt)
 *
 * [Customizing Trusted Certificates](https://square.github.io/okhttp/features/https/#customizing-trusted-certificates-kt-java)
 */
@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        httpInterceptor: Interceptor,
        certificates: HandshakeCertificates
    ): OkHttpClient {
        return OkHttpClient.Builder().apply {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                sslSocketFactory(certificates.sslSocketFactory(), certificates.trustManager)
            }
            writeTimeout(30, TimeUnit.SECONDS)
            readTimeout(30, TimeUnit.SECONDS)
            addInterceptor(httpLoggingInterceptor)
            addInterceptor(httpInterceptor)
        }.build()
    }

    @Provides
    @Singleton
    fun provideListOfCertificates(): List<Int> {
        return listOf(
            R.raw.mocky_ca
        )
    }

    /**
     * Since the SSL certificates for some domains such as mocky.io isn't trusted
     * we need to set the them as a trusted domain on our [OkHttpClient] on [Build.VERSION_CODES.M]
     *
     * NOTE: on android [Build.VERSION_CODES.N] and higher the trusted certificates will be set using
     * `network_security_config.xml` in `AndroidManifest.xml`
     * While on android level [Build.VERSION_CODES.M], trusted certificates need to be set on [OkHttpClient]
     *
     * @param certificateIds provide a list of raw resource ids
     */
    @Provides
    @Singleton
    fun provideTrustedSSLCertificatesApi23(
        @ApplicationContext context: Context,
        certificateIds: List<Int>
    ): HandshakeCertificates {
        return HandshakeCertificates.Builder().apply {
            certificateIds.forEach { certificateId ->
                loadCertificate(
                    context = context,
                    certificateResId = certificateId
                )?.let { trustedCertificate ->
                    addTrustedCertificate(trustedCertificate)
                }
            }
        }.build()
    }

    /**
     * loads a certain certificate pem raw file and generate [X509Certificate]
     * @return null on [CertificateException] if certificate file can't be loaded or contains invalid characters or is empty
     */
    fun loadCertificate(context: Context, @RawRes certificateResId: Int): X509Certificate? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) return null
        try {
            context.resources.openRawResource(certificateResId).use { inputStream ->
                return CertificateFactory.getInstance("X.509")
                    .generateCertificate(inputStream) as X509Certificate
            }
        } catch (e: NotFoundException) {
            // provided raw resource not found
            e.printStackTrace()
            return null
        } catch (e: CertificateException) {
            // invalid certificate
            e.printStackTrace()
            return null
        }
    }

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            setLevel(
                if (BuildConfig.DEBUG) {
                    (HttpLoggingInterceptor.Level.BODY)
                } else {
                    (HttpLoggingInterceptor.Level.NONE)
                }
            )
        }
    }

    /**
     * No specific headers, such as API keys or Authentication tokens are provided from any of our remote endpoint
     */
    @Provides
    @Singleton
    fun provideHttpInterceptor(): Interceptor {
        return Interceptor { chain ->
            val request = chain.request()
            val requestBuilder = request.newBuilder()
            val newRequest = requestBuilder.build()
            return@Interceptor try {
                chain.proceed(newRequest)
            } catch (e: Exception) {
                throw IOException(e.message)
            }
        }
    }
}
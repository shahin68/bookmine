plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.shahin.core.network"
    compileSdk = libs.versions.compileSdkVersion.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdkVersion.get().toInt()

        testInstrumentationRunner = "com.shahin.core.network.HiltTestRunner"

        consumerProguardFiles("consumer-rules.pro")

        buildConfigField ("String", "BASE_URL_MOCKY_IO", "\"https://run.mocky.io/\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    testImplementation(libs.junit)
    testImplementation(libs.androidx.junit)
    testImplementation(libs.androidx.runner)
    testImplementation (libs.kotlinx.coroutines.test)

    // mockito
    testImplementation (libs.mockito.kotlin)

    // retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson.converter)

    // okhttp
    implementation(platform(libs.okhttp3.bom))
    implementation(libs.okhttp3.tls)
    implementation(libs.okhttp3.logging.interceptor)

    // hilt
    implementation(libs.dagger.hilt)
    ksp(libs.dagger.hilt.compiler)
    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.androidx.runner)

    implementation(projects.core.common)
}
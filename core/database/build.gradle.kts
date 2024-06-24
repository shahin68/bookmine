plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.shahin.core.database"
    compileSdk = libs.versions.compileSdkVersion.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdkVersion.get().toInt()

        testInstrumentationRunner = "com.shahin.core.database.HiltTestRunner"
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField ("String", "DATABASE_NAME", "\"books-db\"")
        buildConfigField("String", "DB_PASSPHRASE", "\"my-passphrase123\"")
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
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.kotlinx.coroutines.test)

    // hilt
    implementation(libs.dagger.hilt)
    ksp(libs.dagger.hilt.compiler)
    androidTestImplementation(libs.hilt.android.testing)

    // room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // room - paging
    implementation(libs.androidx.room.paging)

    // Security
    implementation (libs.androidx.security.crypto)

    // Sqlcipher
    implementation (libs.sqlcipher.android)


    implementation(projects.core.common)
}
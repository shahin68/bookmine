plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.shahin.bookmine"
    compileSdk = libs.versions.compileSdkVersion.get().toInt()

    defaultConfig {
        applicationId = "com.shahin.bookmine"
        minSdk = libs.versions.minSdkVersion.get().toInt()
        targetSdk = libs.versions.targetSdkVersion.get().toInt()
        versionCode = 1
        versionName = "1.0"

        buildConfigField ("String", "MOCKY_IDENTIFIER", "\"c7738499-6a6d-49f4-a747-e293c7ee0fea\"")

        testInstrumentationRunner = "com.shahin.bookmine.HiltTestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
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
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    composeCompiler {
        enableStrongSkippingMode = true
    }
}

dependencies {

    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.dexmaker.mockito)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.runner)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // hilt
    implementation(libs.dagger.hilt)
    ksp(libs.dagger.hilt.compiler)
    implementation(libs.dagger.hilt.compose)
    androidTestImplementation(libs.hilt.android.testing)

    // kotlin serialization
    implementation(libs.kotlinx.serialization.json)

    // navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // modules
    implementation(projects.feature.books)
    implementation(projects.feature.bookDetails)
    implementation(projects.core.network)
}
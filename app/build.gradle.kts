plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // KSP (Kotlin Symbol Processing) — used by Room for annotation processing.
    // Preferred over KAPT for Kotlin 2.x projects (faster, no stub generation).
    alias(libs.plugins.ksp)
    // Processes google-services.json and injects Firebase config at build time.
    // Requires google-services.json in the app/ directory (download from Firebase console).
    alias(libs.plugins.google.services)
    // Hilt — generates DI component code via KSP
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.duanett.gymmanager"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.duanett.gymmanager"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // --- Core AndroidX ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // --- Compose ---
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // --- Navigation ---
    implementation(libs.androidx.navigation.compose)

    // --- ViewModel + Compose integration ---
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // --- Room (local database) ---
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)   // Coroutine/Flow extensions
    ksp(libs.androidx.room.compiler)          // Code generation via KSP

    // --- Firebase (cloud sync) ---
    // BOM pins all Firebase library versions so they stay compatible with each other.
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)

    // --- Coroutines ---
    implementation(libs.kotlinx.coroutines.android)

    // --- Hilt (dependency injection) ---
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)                      // Generates Hilt component/injection code
    implementation(libs.hilt.navigation.compose) // hiltViewModel() in Composables


    // --- Testing ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

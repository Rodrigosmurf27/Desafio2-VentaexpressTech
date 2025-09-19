plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.udb.ventaexpress"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.udb.ventaexpress"
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

    // ⬇️ MUY IMPORTANTE: genera ActivityXxxBinding / ItemXxxBinding
    buildFeatures {
        viewBinding = true
    }

    // (opcional pero recomendado) súbelo a 17 si puedes
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions { jvmTarget = "11" }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // RecyclerView (lo usa toda la app)
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // Firebase (según tu catálogo)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)

    // Coroutines (NECESARIO si usas mis controladores con suspend/await)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1")

    // (si realmente usarás estos)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Firebase Auth ya lo tienes
    implementation("com.google.firebase:firebase-auth-ktx:22.3.1")

    // Facebook Login SDK
    implementation("com.facebook.android:facebook-login:16.3.0")
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-parcelize")
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.10"
    id("kotlin-kapt")
    // Dependency Injection Dagger - Hilt
    id("dagger.hilt.android.plugin")

    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.e_commorce_fashions"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.e_commorce_fashions"
        minSdk = 25
        targetSdk = 34
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
        viewBinding = true
    }
    androidResources {
        generateLocaleConfig = true
    }

//    lint {
//        baseline = file("lint-baseline.xml")
//    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.storage)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.androidx.compose.material)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    /// my library's

    // Compose ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.compose.android)

    // Network Calls
    implementation(libs.retrofit)
    implementation (libs.okhttp)

    // Json To Kotlin Object Mapping
    implementation(libs.converter.gson)

    // Image Loading
    implementation(libs.coil.compose)

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Coroutine Lifecycle Scopes
    implementation( "androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4")
    implementation( "androidx.lifecycle:lifecycle-runtime-ktx:2.4.1")

    // Coil
    implementation ("io.coil-kt:coil-compose:2.5.0")

    // system ui controller
    implementation( "com.google.accompanist:accompanist-systemuicontroller:0.32.0")

    // navigation compose
    implementation(libs.androidx.navigation.compose)


    implementation("io.coil-kt:coil-svg:2.5.0")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    implementation("androidx.compose.material:material-icons-extended")

    // responseive ui
    implementation("androidx.compose.material3:material3-window-size-class")

    // WorkManager dependency
    implementation("androidx.work:work-runtime-ktx:2.9.1")

    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    implementation("com.google.dagger:dagger:2.44.2")
    kapt("com.google.dagger:dagger-compiler:2.44.2")
    
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.appcompat:appcompat-resources:1.7.0")

    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("androidx.datastore:datastore:1.1.1")

    implementation("com.google.android.gms:play-services-auth:21.2.0")
    implementation("com.facebook.android:facebook-android-sdk:17.0.1")

    // Dependency Injection Dagger - Hilt
    implementation("com.google.dagger:hilt-android:2.44.2")
    kapt("com.google.dagger:hilt-android-compiler:2.44.2")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03")
    kapt("androidx.hilt:hilt-compiler:1.2.0")

// get user location
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.google.maps.android:maps-compose:2.15.0")
    implementation("com.google.android.gms:play-services-maps:19.0.0")

    // Strip payment
    implementation("com.stripe:stripe-android:20.50.1")
    implementation("com.google.android.gms:play-services-wallet:19.4.0")
//    implementation("com.strip:strip-java:20.50.1")
//    implementation("com.android.volley:volley:1.2.1")

    // paypal payment
//    implementation("com.paypal.android:card-payments:1.5.0")
//    implementation("com.paypal.android:paypal-web-payments:1.5.0")

    // checkout paypal
//    implementation("com.paypal.checkout:android-sdk:1.3.2")

    // Network 
    implementation("com.github.kittinunf.fuel:fuel:3.0.0-alpha03")

//    // pull to refresh
//    implementation("androidx.compose.material3.pulltorefresh:0.3.0")

//    implementation("me.vponomarenko:compose-shimmer:1.0.0")

}
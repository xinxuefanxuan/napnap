plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.work37.napnap"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.work37.napnap"
        minSdk = 28
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(platform("com.google.firebase:firebase-bom:32.0.0")) // Firebase BOM
    implementation("com.google.firebase:firebase-messaging") // FCM dependency
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.room.common)
    implementation(libs.room.runtime)
    implementation(libs.car.ui.lib)
    implementation(libs.viewpager2)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:okhttp:4.9.3")
    implementation ("org.json:json:20210307")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    implementation(libs.activity)
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
    implementation("cn.hutool:hutool-all:5.8.28")
    implementation ("androidx.core:core:1.10.1")
    implementation ("androidx.emoji2:emoji2:1.3.0")  // 可选，支持 emoji
    implementation(platform("com.google.firebase:firebase-bom:33.1.1"))
    implementation("com.google.firebase:firebase-analytics")
//    implementation("com.android.support:appcompat-v7:30.0.0")
    implementation("com.google.firebase:firebase-messaging:latest_version")
    implementation ("com.squareup.okhttp3:okhttp-sse:4.9.3")


    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    annotationProcessor(libs.room.compiler)

}
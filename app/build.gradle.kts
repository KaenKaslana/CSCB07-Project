plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.b07demosummer2024"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.b07demosummer2024"
        minSdk = 24
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
}

    dependencies {
        implementation(libs.appcompat)
        implementation(libs.material)
        implementation(libs.activity)
        implementation(libs.constraintlayout)
        implementation(libs.firebase.database)
        implementation(libs.firebase.storage)
        implementation(libs.viewpager2)
        implementation(libs.fragment)
        //implementation("com.google.firebase:firebase-appcheck-play-integrity:16.0.1")
        implementation(libs.firebase.auth)
        testImplementation(libs.junit)
        androidTestImplementation(libs.ext.junit)
        androidTestImplementation(libs.espresso.core)
        implementation(platform("com.google.firebase:firebase-bom:34.0.0"))
        implementation("com.google.firebase:firebase-analytics")
        implementation(libs.material.v1120)
        implementation("com.google.code.gson:gson:2.13.1")
        implementation("com.google.firebase:firebase-appcheck-debug:17.0.1")
        implementation("com.google.firebase:firebase-appcheck-playintegrity:17.0.1")
        implementation("androidx.work:work-runtime:2.8.0")
        implementation("androidx.core:core-ktx:1.12.0")
}

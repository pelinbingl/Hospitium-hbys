plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.hospitium"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.hospitium"
        minSdk = 24
        targetSdk = 35
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
    buildFeatures{
        viewBinding =true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("com.google.firebase:firebase-auth-ktx:22.3.1")

    // Firebase Firestore
    implementation("com.google.firebase:firebase-firestore-ktx:24.10.1")

    // Firebase Storage (profil foto vs.)
    implementation("com.google.firebase:firebase-storage-ktx:20.3.0")

    // Firebase Messaging (bildirimler)
    implementation("com.google.firebase:firebase-messaging-ktx:23.4.0")

    implementation ("com.google.firebase:firebase-database-ktx:20.3.1")
    implementation ("com.google.firebase:firebase-auth-ktx:22.3.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("com.airbnb.android:lottie:5.2.0")

}
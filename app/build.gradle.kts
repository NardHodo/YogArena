import org.gradle.kotlin.dsl.implementation

plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.yogarena"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.yogarena"
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.annotation)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    //firebase
    implementation(platform("com.google.firebase:firebase-bom:34.4.0"))
    implementation("com.google.firebase:firebase-analytics")

    //camera dependencies
    val cameraxVersions = "1.3.1"
    implementation("androidx.camera:camera-camera2:$cameraxVersions")
    implementation("androidx.camera:camera-lifecycle:$cameraxVersions")
    implementation("androidx.camera:camera-view:$cameraxVersions")
    implementation("androidx.camera:camera-core:$cameraxVersions")

    //tensorflow
    implementation("org.tensorflow:tensorflow-lite:2.17.0") {
        exclude(group = "com.google.ai.edge.litert", module = "litert-api")
    }
    implementation("org.tensorflow:tensorflow-lite-gpu:2.17.0") {
        exclude(group = "com.google.ai.edge.litert", module = "litert-api")
    }
    implementation("org.tensorflow:tensorflow-lite-support:0.4.4") {
        exclude(group = "com.google.ai.edge.litert", module = "litert-api")
    }
    implementation("org.tensorflow:tensorflow-lite-metadata:0.4.4") {
        exclude(group = "com.google.ai.edge.litert", module = "litert-api")
    }
    implementation("org.tensorflow:tensorflow-lite-task-vision:0.4.4") {
        exclude(group = "com.google.ai.edge.litert", module = "litert-api")
    }
}
/*
 * Copyright 2025 Uwe Martin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.screenshot)
    alias(libs.plugins.aboutLibraries)
    alias(libs.plugins.mikepenz.aboutlibraries.android)
    id("org.jetbrains.kotlin.plugin.compose") version "2.3.21"
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp") version "2.3.7"
    id("com.google.dagger.hilt.android")

    // Required for Firebase Crashlytics:
    // Google services Gradle plugin
    id("com.google.gms.google-services")
    // Crashlytics Gradle plugin
    id("com.google.firebase.crashlytics")
}

kotlin {
    jvmToolchain(jdkVersion = 21)
    compilerOptions {
        freeCompilerArgs.add("-XXLanguage:+PropertyParamAnnotationDefaultTargetMode")
    }
}

android {
    namespace = "com.laimburggasse.aquariumcontrol"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.laimburggasse.aquariumcontrol"
        minSdk = 26
        targetSdk = 37
        versionCode = 19
        versionName = "3.3.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["clearPackageData"] = "false"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
        }
    }
    compileOptions {
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "2.2.21"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    @Suppress("UnstableApiUsage")
    experimentalProperties["android.experimental.enableScreenshotTest"] = true
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.paging.common)
    implementation(libs.androidx.paging.compose)
    implementation(libs.androidx.espresso.core)
    implementation(libs.kotlinx.serialization.json.v162)
    implementation(libs.kotlin.stdlib.jdk8.v1922)
    implementation(libs.androidx.tracing.perfetto.handshake)
    implementation(libs.androidx.junit.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // for Dagger/Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(project(":balling"))
    implementation(project(":common"))
    implementation(project(":controller"))
    implementation(project(":feed"))
    implementation(project(":heating"))
    implementation(project(":info"))
    implementation(project(":summary"))
    implementation(project(":refill"))
    implementation(project(":schedule"))
    implementation(project(":timedata"))
    implementation(project(":ventilation"))

    // BoM for the Firebase platform
    implementation(platform(libs.firebase.bom))

    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
}
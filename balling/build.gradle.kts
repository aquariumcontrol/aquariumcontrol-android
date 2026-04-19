plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.screenshot)
    id("org.jetbrains.kotlin.plugin.compose") version "2.3.20"
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp") version "2.2.20-2.0.3"
    id("com.google.dagger.hilt.android")
}

kotlin {
    jvmToolchain(jdkVersion = 21)
}

android {
    namespace = "com.laimburggasse.aquariumcontrol.balling"
    compileSdk = 37
    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    testOptions {
        //execution = "ANDROIDX_TEST_ORCHESTRATOR"
        screenshotTests {
            imageDifferenceThreshold = 0.0002f // 0.02%
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
    }
    compileOptions {
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }

    @Suppress("UnstableApiUsage")
    testFixtures.enable = true
    experimentalProperties["android.experimental.enableScreenshotTest"] = true
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
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.espresso.core)
    implementation(libs.kotlinx.serialization.json.v162)
    implementation(libs.okhttp)
    implementation(libs.kotlin.stdlib.jdk8.v1922)
    implementation(libs.androidx.tracing.perfetto.handshake)
    implementation(libs.androidx.junit.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // for compose preview snapshot testing
    screenshotTestImplementation(libs.screenshot.validation.api)
    screenshotTestImplementation(libs.androidx.ui.tooling)

    // for Dagger/Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.okhttp)

    implementation(libs.retrofit)
    implementation(libs.converter.kotlinx.serialization)

    implementation(project(":common"))
    androidTestImplementation(testFixtures(notation = project(":common")))
    androidTestImplementation(libs.androidx.junit)

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
}
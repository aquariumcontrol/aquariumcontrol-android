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
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.screenshot)
    id("org.jetbrains.kotlin.plugin.compose") version "2.3.21"
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp") version "2.3.7"
    id("com.google.dagger.hilt.android")
    id("jacoco")
}

kotlin {
    jvmToolchain(jdkVersion = 21)
}

android {
    namespace = "com.laimburggasse.aquariumcontrol.timedata"
    compileSdk = 37
    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        debug {
            // AGP 9.0+ specific: Enable coverage collection for Unit Tests
            enableUnitTestCoverage = true
            // Optional: Also enable for instrumentation tests
            // enableAndroidTestCoverage = true
        }
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
    testOptions {
        //execution = "ANDROIDX_TEST_ORCHESTRATOR"
        screenshotTests {
            imageDifferenceThreshold = 0.010f
        }
        unitTests.all {
            // Essential for Kotlin: include classes with no source location (like lambdas/inlines)
            extensions.findByType<JacocoTaskExtension>()?.apply {
                isIncludeNoLocationClasses = true
                // Prevents JaCoCo from conflicting with other agents
                excludes = listOf("jdk.internal.*")
            }
        }
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
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

    // for charts (time data. heating stats and feed profile)
    implementation (libs.mpandroidchart)

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

    implementation(platform(libs.firebase.bom))
    implementation(libs.google.firebase.crashlytics)
    implementation(libs.google.firebase.analytics)
}

jacoco {
    toolVersion = "0.8.12" // version compatible with JDK 21+
}

// configuration of code coverage reports
val jacocoTestReport by tasks.registering(JacocoReport::class) {
    dependsOn("testDebugUnitTest") // Run tests before generating report

    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    // Exclude generated code (Hilt, Dagger, etc.) from coverage math
    val fileFilter = listOf(
        "**/R.class", "**/R$*.class", "**/BuildConfig.*", "**/Manifest*.*",
        "**/*_Hilt*.class", "**/Hilt_*.class", "**/*_Factory.class",
        "**/*_MembersInjector.class", "**/*Module_*Factory.class",
        $$"**/*$Serializer.*", "**/DataBinderMapperImpl.*"
    )

    val debugTree = fileTree("${project.layout.buildDirectory.get()}/tmp/kotlin-classes/debug") {
        exclude(fileFilter)
    }

    val mainSrc = "${project.projectDir}/src/main/kotlin"

    sourceDirectories.setFrom(files(mainSrc))
    classDirectories.setFrom(files(debugTree))

    // Path to the .exec file generated by AGP 9.0+
    executionData.setFrom(fileTree(project.layout.buildDirectory.get()) {
        include("outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
        include("jacoco/testDebugUnitTest.exec")
    })
}
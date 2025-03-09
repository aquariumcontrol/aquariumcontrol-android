// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.20" apply false // Use your actual Kotlin version
    id("com.android.library") version "8.1.4" apply false
}

buildscript {
    extra.apply {
        set("room_version", "2.6.0")
    }
    dependencies {
//        classpath(libs.r8)
        classpath(libs.kotlin.gradle.plugin)
    // classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.20")
        // ... other dependencies ...
    }
}
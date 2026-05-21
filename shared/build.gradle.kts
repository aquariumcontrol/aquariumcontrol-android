plugins {
    kotlin("multiplatform")
    id("org.jetbrains.kotlin.plugin.serialization")
}

kotlin {
    jvmToolchain(21)
    
    jvm()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    // Configure the framework for iOS integration
    val iosTargets = listOf(iosX64(), iosArm64(), iosSimulatorArm64())
    iosTargets.forEach { target ->
        target.binaries.framework {
            baseName = "SharedAquariumControl"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.json.v162)
            implementation(libs.androidx.room.runtime)
        }
    }
}

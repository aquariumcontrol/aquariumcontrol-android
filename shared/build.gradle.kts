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

    val iosTargets = listOf(iosX64(), iosArm64(), iosSimulatorArm64())
    iosTargets.forEach { target ->
        target.binaries.framework {
            baseName = "SharedAquariumControl" // name of the import in Swift
            isStatic = true // simpler linking
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.json.v162)
            implementation(libs.androidx.room.runtime)
        }
    }
}

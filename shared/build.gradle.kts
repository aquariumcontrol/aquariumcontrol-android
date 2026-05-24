plugins {
    id("com.android.kotlin.multiplatform.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.serialization")
    alias(libs.plugins.androidx.room)
    alias(libs.plugins.ksp)
}

kotlin {
    android {
        namespace = "com.laimburggasse.aquariumcontrol.shared"
        compileSdk = 37
        minSdk = 26
    }

    jvmToolchain(21)
    
    jvm()
    iosArm64()
    iosSimulatorArm64()

    val iosTargets = listOf(iosArm64(), iosSimulatorArm64())
    iosTargets.forEach { target ->
        target.binaries.framework {
            baseName = "SharedAquariumControl"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.androidx.datastore.preferences)
            implementation(libs.kotlinx.serialization.json.v162)
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.room.paging)
            implementation(libs.androidx.sqlite.bundled)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.auth)
            implementation(libs.ktor.client.logging)
            implementation(libs.kotlin.inject.runtime)
            implementation(libs.androidx.paging.common)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.io.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

room {
    generateKotlin = true
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    add("kspCommonMainMetadata", libs.kotlin.inject.compiler.ksp)
    // // add("kspCommonMainMetadata", libs.androidx.room.compiler)
    
    add("kspAndroid", libs.kotlin.inject.compiler.ksp)
    add("kspAndroid", libs.androidx.room.compiler)
    
    add("kspJvm", libs.kotlin.inject.compiler.ksp)
    add("kspJvm", libs.androidx.room.compiler)

    add("kspIosArm64", libs.kotlin.inject.compiler.ksp)
    add("kspIosArm64", libs.androidx.room.compiler)
    
    add("kspIosSimulatorArm64", libs.kotlin.inject.compiler.ksp)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
}


ksp {
    arg("room.generateKotlin", "true")
}

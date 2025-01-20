import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.ktor)
}

kotlin {
    jvm {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_1_8
        }
    }

    sourceSets {
        jvmMain.dependencies {
            implementation(project(":common"))
            implementation(libs.kotlinx.serialization)
            implementation(libs.bundles.ktor.server)
        }
    }
}
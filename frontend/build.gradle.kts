@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.ktor)
}

kotlin {
    js {
        moduleName = "rstcoversweb"
        browser {
            commonWebpackConfig {
                outputFileName = "rstcoversweb.js"
            }
        }
        binaries.executable()
        useEsModules()
    }

    sourceSets {
        jsMain.dependencies {
            implementation(project(":common"))
            implementation(compose.runtime)
            implementation(compose.html.core)
            implementation(compose.ui)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.components.resources)
            implementation(libs.kotlinx.serialization)
            implementation(libs.bundles.ktor.client)
        }
    }
}
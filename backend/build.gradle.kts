import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.serialization)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll(
            "-opt-in=kotlin.uuid.ExperimentalUuidApi",
            "-opt-in=kotlin.time.ExperimentalTime",
            "-Xcontext-parameters"
        )
    }

    jvm {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        binaries {
            executable {
                mainClass.set("io.github.nicolasfara.MainKt")
            }
        }
        compilerOptions {
            jvmTarget = JvmTarget.JVM_1_8
        }
    }

    sourceSets {
        jvmMain.dependencies {
            implementation(project(":shared"))
            implementation(libs.exposed.core)
            implementation(libs.exposed.r2dbc)
            implementation(libs.postgresql.r2dbc)
            implementation(libs.bundles.ktor)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.arrow.core)
            implementation(libs.arrow.fx.coroutines)
        }
    }
}

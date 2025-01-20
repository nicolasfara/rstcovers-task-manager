import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    alias(libs.plugins.dokka)
    alias(libs.plugins.gitSemVer)
    alias(libs.plugins.kotlin.qa)
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.multiJvmTesting)
    alias(libs.plugins.publishOnCentral)
    alias(libs.plugins.taskTree)
}

group = "it.nicolasfarabegoli"

val Provider<PluginDependency>.id: String get() = get().pluginId

subprojects {
    with(rootProject.libs.plugins) {
        apply(plugin = dokka.id)
        apply(plugin = gitSemVer.id)
        apply(plugin = kotlin.qa.id)
        apply(plugin = kotlinx.serialization.id)
        apply(plugin = multiJvmTesting.id)
        apply(plugin = publishOnCentral.id)
        apply(plugin = taskTree.id)
    }

    repositories {
        google()
        mavenCentral()
    }

    multiJvm {
        jvmVersionForCompilation.set(21)
    }

//    signing {
//        if (System.getenv("CI") == "true") {
//            val signingKey: String? by project
//            val signingPassword: String? by project
//            useInMemoryPgpKeys(signingKey, signingPassword)
//        }
//    }

//    publishOnCentral {
//        repoOwner.set("DanySK")
//        projectLongName.set("Template for Kotlin Multiplatform Project")
//        projectDescription.set("A template repository for Kotlin Multiplatform projects")
//        repository("https://maven.pkg.github.com/danysk/${rootProject.name}".lowercase()) {
//            user.set("DanySK")
//            password.set(System.getenv("GITHUB_TOKEN"))
//        }
//        publishing {
//            publications {
//                withType<MavenPublication> {
//                    pom {
//                        developers {
//                            developer {
//                                name.set("Danilo Pianini")
//                                email.set("danilo.pianini@gmail.com")
//                                url.set("http://www.danilopianini.org/")
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
}

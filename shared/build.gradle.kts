import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.ktlintGradle)
    alias(libs.plugins.detekt)
}

ktlint {
    filter {
        exclude { entry -> entry.file.absolutePath.contains("/build/") }
    }
}

val createBuildEditorConfig by tasks.registering {
    val editorConfigFile = layout.buildDirectory.file(".editorconfig")
    outputs.file(editorConfigFile)
    doLast {
        editorConfigFile.get().asFile.writeText(
            "root = true\n[*.kt]\nktlint_standard_import-ordering = disabled\nktlint_standard_indent = disabled\n",
        )
    }
}

afterEvaluate {
    tasks.matching { t -> t.name.matches(Regex("ktlint.*SourceSetCheck")) }
        .configureEach { dependsOn(createBuildEditorConfig) }
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }

    jvm()

    androidLibrary {
        namespace = "com.span.ashless.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
        androidResources {
            enable = true
        }
        withHostTest {
            isIncludeAndroidResources = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.kotlinx.datetime)
        }
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.koin.android)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}

val detektAll by tasks.registering(Detekt::class) {
    description = "Runs detekt on all Kotlin sources."
    buildUponDefaultConfig = true
    config.setFrom(rootProject.file("detekt.yml"))
    source(fileTree("src") { include("**/*.kt") })
    classpath.setFrom()
    parallel = true
    reports {
        html.required.set(false)
        xml.required.set(false)
        txt.required.set(false)
    }
}

tasks.named("check") {
    dependsOn(detektAll)
}

// iOS tests require Xcode. Set ios.tests.enabled=true in gradle.properties when Xcode is available.
val iosTestsEnabled = providers.gradleProperty("ios.tests.enabled").map { it.toBoolean() }.getOrElse(false)
if (!iosTestsEnabled) {
    tasks.matching { t -> t.name.contains("linkDebugTestIos") || t.name.contains("iosSimulatorArm64Test") }
        .configureEach { enabled = false }
}

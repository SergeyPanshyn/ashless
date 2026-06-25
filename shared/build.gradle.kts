import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.ksp)
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
    compilerOptions {
        freeCompilerArgs.addAll(
            "-opt-in=kotlin.uuid.ExperimentalUuidApi",
            "-opt-in=kotlin.time.ExperimentalTime",
            "-Xexpect-actual-classes",
        )
    }

    // iOS targets are declared for future use but disabled when Xcode is unavailable.
    // CMP 1.11.1 K/N klibs require Kotlin 2.3.x; re-enable once KSP supports 2.3.x.
    val iosEnabled = providers.gradleProperty("ios.enabled").map { it.toBoolean() }.getOrElse(false)
    if (iosEnabled) {
        listOf(
            iosArm64(),
            iosSimulatorArm64(),
        ).forEach { iosTarget ->
            iosTarget.binaries.framework {
                baseName = "Shared"
                isStatic = true
            }
        }
    }

    jvm()

    androidTarget {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(libs.compose.ui)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.kotlinx.datetime)
            implementation(libs.androidx.datastore.core)
        }
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.koin.android)
            implementation(libs.androidx.datastore.android)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.turbine)
            implementation(libs.kotlinx.coroutines.test)
        }
        jvmTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.turbine)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.kotlinx.datetime)
        }
    }
}

android {
    namespace = "com.span.ashless.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(libs.compose.uiTooling)
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspJvm", libs.androidx.room.compiler)
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

// iOS targets disabled until KSP supports Kotlin 2.3.x (CMP 1.11.1 K/N ABI requirement).
// Enable via gradle.property ios.enabled=true once constraints are resolved.

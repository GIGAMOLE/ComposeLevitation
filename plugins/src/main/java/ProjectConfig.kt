@file:Suppress("unused")

import org.gradle.api.JavaVersion
import org.gradle.jvm.toolchain.JavaLanguageVersion

object ProjectConfig {
    const val versionCode = 1
    const val versionName = "1.0.0"

    const val namespace = "com.gigamole.composelevitation"
    const val group = "com.github.GIGAMOLE"
    const val artifact = "ComposeLevitation"
    const val publication = "release"

    const val compileSdk = 33
    const val targetSdk = 33
    const val minSdk = 24

    val javaCompileVersion = JavaVersion.VERSION_17
    private val javaCompileVersionText = javaCompileVersion.toString()
    val javaLanguageVersion: JavaLanguageVersion = JavaLanguageVersion.of(javaCompileVersionText)
    val kotlinJvmTarget = javaCompileVersionText
}
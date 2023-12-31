@file:Suppress("UnstableApiUsage")

@Suppress(
    "DSL_SCOPE_VIOLATION",
    "MISSING_DEPENDENCY_CLASS",
    "UNRESOLVED_REFERENCE_WRONG_RECEIVER",
    "FUNCTION_CALL_EXPECTED"
)

plugins {
    id("composelevitation.library")
    `maven-publish`
}

group = ProjectConfig.group
version = ProjectConfig.versionName

publishing {
    publications {
        register<MavenPublication>(ProjectConfig.publication) {
            groupId = ProjectConfig.group
            artifactId = ProjectConfig.artifact
            version = ProjectConfig.versionName

            afterEvaluate {
                from(components[ProjectConfig.publication])
            }
        }
    }
}

android {
    namespace = ProjectConfig.namespace

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.version.get()
    }

    publishing {
        singleVariant(ProjectConfig.publication) {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {
    implementation(libs.androidx.ktx)

    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)

    api(libs.compose.shadows.plus)

    debugImplementation(libs.bundles.debug.compose)
}
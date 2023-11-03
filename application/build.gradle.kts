import org.jetbrains.compose.desktop.application.dsl.TargetFormat

val ktor_version: String by project
val nav_version: String by project

plugins {
    kotlin("jvm") version "1.9.10"
    id("org.jetbrains.compose") version "1.5.1"
    kotlin("plugin.serialization") version "1.9.0"
}

group = "com.theappengers"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(project(":models"))
    implementation(compose.desktop.currentOs)

    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
}


compose.desktop {
    application {
        mainClass = "AppengerWhiteboardKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "theappengers"
            packageVersion = "1.1.0"
        }
    }
}

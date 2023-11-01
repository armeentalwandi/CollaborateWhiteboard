plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.9.0"
}

repositories {
    mavenCentral()
}

dependencies {
    // Add other dependencies that are necessary for your models.
    // For example, if you're serializing your data models:
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.3.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
}
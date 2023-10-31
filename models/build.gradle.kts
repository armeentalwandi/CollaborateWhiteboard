plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    // Add other dependencies that are necessary for your models.
    // For example, if you're serializing your data models:
     implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.3.1")
}
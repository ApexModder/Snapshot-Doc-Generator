pluginManagement {
    repositories {
        maven("https://maven.apexmodder.com/proxy")
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

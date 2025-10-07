plugins {
    java

    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.9"
    id("com.gradleup.shadow") version "9.0.2"
}

val IS_CI = providers.environmentVariable("CI").map(String::toBoolean).getOrElse(false)

group = "dev.apexstudios"
version = providers.environmentVariable("VERSION").getOrElse("9.9.999")
base.archivesName = "snapshotgen"
println("SnapshotGen: $version")

idea.module {
    if(!IS_CI) {
        isDownloadSources = true
        isDownloadJavadoc = true
    }

    excludeDirs.addAll(files(
        ".gradle",
        ".idea",
        "build",
        "gradle"
    ))
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
        vendor.set(JvmVendorSpec.ADOPTIUM)
    }

    withSourcesJar()
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    maven("https://maven.apexstudios.dev/releases")
    maven("https://maven.apexstudios.dev/private")
    maven("https://libraries.minecraft.net")
}

dependencies {
    implementation("com.mojang:datafixerupper:8.0.16")
    // implementation("com.google.code.findbugs:jsr305:3.0.2")
    // implementation("com.google.code.gson:gson:2.11.0")
    // implementation("com.google.errorprone:error_prone_annotations:2.28.0")
    // implementation("com.google.guava:failureaccess:1.0.2")
    // implementation("com.google.guava:guava:33.3.1-jre")
    // implementation("com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava")
    // implementation("commons-codec:commons-codec:1.17.1")
    // implementation("commons-io:commons-io:2.17.0")
    // implementation("commons-logging:commons-logging:1.3.4")
    // implementation("it.unimi.dsi:fastutil:8.5.15")
    implementation("net.sf.jopt-simple:jopt-simple:5.0.4")
    // implementation("org.apache.commons:commons-compress:1.27.1")
    implementation("org.apache.commons:commons-lang3:3.17.0")
    // implementation("org.apache.httpcomponents:httpclient:4.5.14")
    // implementation("org.apache.httpcomponents:httpcore:4.4.16")
    implementation("org.apache.logging.log4j:log4j-api:2.24.1")
    implementation("org.apache.logging.log4j:log4j-core:2.24.1")
    // implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.24.1")
    // implementation("org.apache.maven:maven-artifact:3.9.9")
    implementation("org.jetbrains:annotations:26.0.2")
    // implementation("org.jline:jline-reader:3.20.0")
    // implementation("org.jline:jline-terminal:3.20.0")
    // implementation("org.joml:joml:1.10.8")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.jar {
    manifest {
        attributes(
            Pair("Main-Class", "${project.group}.snapshot.Main")
        )
    }
}

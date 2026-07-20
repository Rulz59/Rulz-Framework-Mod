import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.fabricmc.loom.task.RemapJarTask
import org.gradle.api.tasks.Copy

plugins {
    id("dev.architectury.loom")
    id("com.github.johnrengelman.shadow")
    kotlin("jvm")
    id("java")
}

architectury {
    platformSetupLoomIde()
    neoForge()
}

// 1. RESTORE CONFIGURATIONS (Required for Architectury Loom)
val common: Configuration by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
}

val compileClasspath by configurations.getting {
    extendsFrom(common)
}

val runtimeClasspath by configurations.getting {
    extendsFrom(common)
}

val developmentNeoForge by configurations.getting {
    extendsFrom(common)
}

val shadowBundle by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
}

repositories {
    maven {
        name = "NeoForged"
        url = uri("https://maven.neoforged.net/releases")
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    mappings(loom.officialMojangMappings())

    neoForge("net.neoforged:neoforge:${property("neoforge_version")}")

    implementation("dev.architectury:architectury-neoforge:${property("architectury_api_version")}")

    // 2. RESTORE COMMON PROJECT BINDINGS
    // Points Architectury Loom to common subproject sources
    "common"(project(":common", configuration = "namedElements")) {
        isTransitive = false
    }

    // Bundles common into shadow/remap tasks for built JARs
    shadowBundle(project(":common", configuration = "transformProductionNeoForge")) {
        isTransitive = false
    }
}

tasks.named<Copy>("processResources") {
    inputs.property("version", project.version)

    filesMatching("META-INF/neoforge.mods.toml") {
        expand("version" to project.version)
    }
}

tasks.named<ShadowJar>("shadowJar") {
    configurations = listOf(shadowBundle)
    archiveClassifier.set("dev-shadow")
}

tasks.named<RemapJarTask>("remapJar") {
    inputFile.set(tasks.named<ShadowJar>("shadowJar").flatMap { it.archiveFile })
    dependsOn(tasks.named("shadowJar"))
    archiveClassifier.set("neoforge")
}
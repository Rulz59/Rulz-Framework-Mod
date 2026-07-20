import net.fabricmc.loom.api.LoomGradleExtensionAPI
import org.gradle.api.JavaVersion
import org.gradle.api.publish.maven.MavenPublication

plugins {
    // Kotlin
    kotlin("jvm") version "2.0.20"

    // Architectury core plugin
    id("architectury-plugin") version "3.4-SNAPSHOT"

    // Loom is declared here but applied per-module (Fabric / NeoForge)
    id("dev.architectury.loom") version "1.9.436" apply false

    // Needed for `java {}` and `withSourcesJar()`
    id("java")

    // Needed for `publishing {}` / `publications {}` / MavenPublication
    id("maven-publish")

    // Shadow plugin for creating fat jars (used in Fabric / NeoForge modules)
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
}

// This was inside `plugins {}` before — that’s why `base` exploded
base {
    archivesName.set("${rootProject.property("archives_name")}-${project.name}")
}

val minecraftVersion = rootProject.property("minecraft_version").toString()

// If this root script is meant to configure all subprojects:
subprojects {
    // Make sure these plugins are actually applied to child modules
    apply(plugin = "java")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "architectury-plugin")
    apply(plugin = "maven-publish")

    group = rootProject.property("maven_group").toString()
    version = rootProject.property("mod_version").toString()

    // Archives name per subproject
    base {
        archivesName.set("${rootProject.property("archives_name")}-${project.name}")
    }

    repositories {
        mavenCentral()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev/")
        maven("https://maven.neoforged.net/releases")
    }

    // Loom extension is only present in modules where the Loom plugin is applied
    val loom = extensions.findByType(LoomGradleExtensionAPI::class.java)
    if (loom != null) {
        // This mirrors your Groovy `loom { silentMojangMappingsLicense() }`
        loom.silentMojangMappingsLicense()

        dependencies {
            // Minecraft + Mojmap mappings
            add("minecraft", "com.mojang:minecraft:$minecraftVersion")
            add("mappings", loom.officialMojangMappings())
        }
    }

    java {
        withSourcesJar()
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(21)
    }

    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                artifactId = base.archivesName.get()
                from(components["java"])
            }
        }
    }
}

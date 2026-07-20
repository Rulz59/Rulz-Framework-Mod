pluginManagement {
    repositories {
        maven("https://maven.architectury.dev/")
        maven("https://maven.fabricmc.net/")
        maven("https://maven.neoforged.net/releases")
        gradlePluginPortal()
    }
    plugins {
        // Safe direct property resolution for settings
        val loomVersion = settings.extra.properties["loom_version"].toString()
        val shadowVersion = settings.extra.properties["shadow_version"].toString()
        val kotlinVersion = settings.extra.properties["kotlin_version"].toString()
        val architecturyPluginVersion = settings.extra.properties["architectury_plugin_version"].toString()

        id("dev.architectury.loom") version loomVersion
        id("com.github.johnrengelman.shadow") version shadowVersion
        id("architectury-plugin") version architecturyPluginVersion
        kotlin("jvm") version kotlinVersion
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://maven.architectury.dev/")
        maven("https://maven.fabricmc.net/")
        maven("https://maven.neoforged.net/releases")
    }
}

rootProject.name = settings.extra.properties["archives_name"].toString()
include("common", "fabric", "neoforge")
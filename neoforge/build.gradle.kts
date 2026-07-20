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
    val dynamicIconPath = "assets/${project.property("archives_name")}/icon.png"

    inputs.property("version", project.version)
    inputs.property("mod_id", project.property("archives_name"))
    inputs.property("mod_name", project.property("mod_name"))
    inputs.property("mod_description", project.property("mod_description"))
    inputs.property("mod_authors", project.property("mod_authors"))
    inputs.property("mod_license", project.property("mod_license"))
    inputs.property("mod_icon", dynamicIconPath)
    inputs.property("mod_homepage", project.property("mod_homepage"))
    inputs.property("mod_issues", project.property("mod_issues"))

    from(project(":common").sourceSets.main.get().resources)

    filesMatching("META-INF/neoforge.mods.toml") {
        expand(
            "version" to project.version,
            "mod_id" to project.property("archives_name"),
            "mod_name" to project.property("mod_name"),
            "mod_description" to project.property("mod_description"),
            "mod_authors" to project.property("mod_authors"),
            "mod_license" to project.property("mod_license"),
            "mod_icon" to dynamicIconPath,
            "mod_homepage" to project.property("mod_homepage"),
            "mod_issues" to project.property("mod_issues")
        )
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}


tasks.named<ShadowJar>("shadowJar") {
    configurations = listOf(shadowBundle)
    archiveClassifier.set("dev-shadow")
    from(project(":common").sourceSets.main.get().output)
}

tasks.named<RemapJarTask>("remapJar") {
    inputFile.set(tasks.named<ShadowJar>("shadowJar").flatMap { it.archiveFile })
    dependsOn(tasks.named("shadowJar"))
    archiveClassifier.set("neoforge")
}
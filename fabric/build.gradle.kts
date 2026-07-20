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
    fabric()
}

configurations {
    val common by creating {
        isCanBeResolved = true
        isCanBeConsumed = false
    }

    val compileClasspath by getting {
        extendsFrom(common)
    }

    val runtimeClasspath by getting {
        extendsFrom(common)
    }

    val developmentFabric by getting {
        extendsFrom(common)
    }

    val shadowBundle by creating {
        isCanBeResolved = true
        isCanBeConsumed = false
        extendsFrom(configurations.implementation.get())
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    mappings(loom.officialMojangMappings())

    modImplementation("net.fabricmc:fabric-loader:${property("fabric_loader_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_api_version")}")
    modImplementation("dev.architectury:architectury-fabric:${property("architectury_api_version")}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${property("kotlin_version")}")

    implementation(project(":common", configuration = "namedElements")) {
        isTransitive = false
    }

    shadow(project(":common", configuration = "transformProductionFabric"))
/*
    modLocalRuntime(fileTree("src/main/resources/mods") {
        include("*.jar")
    })
*/}

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
    filesMatching("fabric.mod.json") {
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
    configurations = listOf(project.configurations.getByName("shadowBundle"))
    archiveClassifier.set("dev-shadow")
}

tasks.named<RemapJarTask>("remapJar") {
    inputFile.set(tasks.named<ShadowJar>("shadowJar").flatMap { it.archiveFile })
}

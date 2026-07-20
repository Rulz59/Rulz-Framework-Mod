import net.fabricmc.loom.api.LoomGradleExtensionAPI
import org.gradle.api.JavaVersion
import org.gradle.api.publish.maven.MavenPublication

plugins {
    kotlin("jvm")
    id("architectury-plugin")
    id("dev.architectury.loom") apply false
    id("com.github.johnrengelman.shadow") apply false
    id("java")
    id("maven-publish")
}

// This was inside `plugins {}` before — that’s why `base` exploded
base {
    archivesName.set("${rootProject.property("archives_name")}-${project.name}")
}

val minecraftVersion = rootProject.property("minecraft_version").toString()

// Mappings of the versions from gradle.properties to be used in each mod loader’s build.gradle.kts
extra["resourceProperties"] = mapOf(
    "version" to rootProject.property("mod_version"),
    "mod_id" to rootProject.property("archives_name"),
    "mod_name" to rootProject.property("mod_name"),
    "mod_description" to rootProject.property("mod_description"),
    "mod_authors" to rootProject.property("mod_authors"),
    "mod_license" to rootProject.property("mod_license"),
    "mod_icon" to "assets/${rootProject.property("archives_name")}/icon.png",
    "mod_homepage" to rootProject.property("mod_homepage"),
    "mod_issues" to rootProject.property("mod_issues")
)

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

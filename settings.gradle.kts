pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://plugins.gradle.org/m2/")
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev/")
        maven("https://files.minecraftforge.net/maven/")
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "dev.architectury.loom") {
                useModule("dev.architectury:architectury-loom:${requested.version}")
            }
        }
    }
}


dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev/")
        maven("https://files.minecraftforge.net/maven/")
    }
}

rootProject.name = "rulzframework"

include("common", "fabric", "neoforge")

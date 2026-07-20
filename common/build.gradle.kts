plugins {
    id("architectury-plugin")
    id("dev.architectury.loom")
    kotlin("jvm")
    id("java")
}

architectury {
    common("fabric", "neoforge")
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    mappings(loom.officialMojangMappings())

    implementation("dev.architectury:architectury:${property("architectury_api_version")}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${property("kotlin_version")}")
}

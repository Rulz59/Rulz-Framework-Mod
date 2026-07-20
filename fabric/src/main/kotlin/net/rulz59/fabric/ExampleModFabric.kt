package net.rulz59.fabric

import net.fabricmc.api.ModInitializer
import net.rulz59.ExampleMod

class ExampleModFabric : ModInitializer {
    override fun onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        ExampleMod.init()
    }
}

pluginManagement {
	repositories {
		mavenCentral()
		gradlePluginPortal()
        maven("https://maven.kikugie.dev/releases")
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev")
        maven("https://maven.minecraftforge.net")
        maven("https://maven.neoforged.net/releases/")
	}
}

plugins {
    id("gg.meza.stonecraft") version "1.+"
    id("dev.kikugie.stonecutter") version "0.6.+"
}

stonecutter {
    centralScript = "build.gradle.kts"
    kotlinController = true
    shared {
        fun mc(version: String, vararg loaders: String) {
            // Version directories "1.20.2-fabric", "1.20.2-forge", etc.
            for (it in loaders) vers("$version-$it", version)
        }

        // Forge Ages
        // 1.16.5 - 1.17.1
        mc("1.16.5", "forge")
        // 1.18   - 1.18.2
        mc("1.18", "forge", "fabric")
        // GSON Changes
        // 1.19   - 1.20.5
        mc("1.19", "forge", "fabric")
        // Neo Star
        // 1.20.6 - 1.21.10
        mc("1.20.6", "forge", "fabric", "neoforge")

        vcsVersion = "1.20.6-fabric"
    }
    create(rootProject)
}

rootProject.name = "Config Manager"
import gg.meza.stonecraft.mod
import java.util.Properties

plugins {
    id("gg.meza.stonecraft")
}

// configure the maven publication
publishMods {
    dryRun = false
    changelog = file("../../changelogs/${project.property("mod.version")}.md").readText()

    modrinth {
        if (mod.isFabric) {
            requires("fabric-api")
        }
    }
}

modSettings {
    variableReplacements = mapOf(
        "fabricLoaderVersion" to mod.prop("fabricLoaderVersion", ">=0.18.4")
    )
}

repositories {
    mavenCentral()
}

dependencies {
    // json5-java
    implementation("de.marhali:json5-java:3.0.0")
}

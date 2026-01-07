import gg.meza.stonecraft.mod
import java.util.Properties

plugins {
    id("gg.meza.stonecraft")
}

fun toJsonOrSingle(input: String): String {
    val trimmed = input.trim()

    return if ("," in trimmed) {
        trimmed.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .joinToString(prefix = "[", postfix = "]") { "\"$it\"" }
    } else {
        "\"$trimmed\""
    }
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
        "license" to mod.prop("mod.license"),
        "authors" to mod.prop("mod.authors"),
        "forgeLoaderVersion" to mod.prop("forge_loader"),
        "fabricLoaderVersion" to mod.prop("loader_version"),
        "minecraftVersionRange" to toJsonOrSingle(mod.prop("additional_versions"))
    )
}

repositories {
    mavenCentral()
}

dependencies {
    // json5-java
    implementation("de.marhali:json5-java:3.0.0")
}

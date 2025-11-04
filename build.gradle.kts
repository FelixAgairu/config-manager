import gg.meza.stonecraft.mod
import java.util.Properties

plugins {
    id("gg.meza.stonecraft")
}

// configure the maven publication
publishMods {
    dryRun = false
    changelog = "See GitHub for changelog"

    modrinth {
        if (mod.isFabric) {
            requires("fabric-api")
        }
    }
}

modSettings {
    variableReplacements = mapOf(
        "fabricLoaderVersion" to mod.prop("fabricLoaderVersion", ">=0.17.3")
    )
}

repositories {
    mavenCentral()
}

dependencies {
    // json5-java
    implementation("de.marhali:json5-java:3.0.0")
}

// Add version number auto increment
// 增加版本号自动增加
tasks.register("incrementVersionTask") {
    doLast {
        val propertiesFile = file("gradle.properties")
        val properties = Properties().apply {
            load(propertiesFile.reader())
        }

        val oldVersion = properties.getProperty("mod.version")
        val parts = oldVersion.split(".").toMutableList()
        parts[2] = (parts[2].toInt() + 1).toString()
        val newVersion = parts.joinToString(".")

        properties.setProperty("mod.version", newVersion)
        properties.store(propertiesFile.writer(), null)

        println("Version incremented from $oldVersion to $newVersion")
    }
}

// Increase version number after build
// 项目构建完成后增加版本号
//tasks.named("build") {
//    finalizedBy("incrementVersionTask")
//}

**[English](/README.md)** [简体中文](README-zh_cn.md)

Get it from:

![Modrinth Downloads](https://img.shields.io/modrinth/dt/config-manager?logo=modrinth&label=Downloads%20%E4%B8%8B%E8%BD%BD%E9%87%8F&link=https%3A%2F%2Fmodrinth.com%2Fmod%2Fconfig-manager)


# Config Manager
A serializer using GSON and load/save config files.

## Use
- Gradle
```groovy
repositories {
    exclusiveContent {
        forRepository {
            maven {
                name = "Modrinth"
                url = "https://api.modrinth.com/maven"
            }
        }
        filter {
            includeGroup "maven.modrinth"
        }
    }
}

dependencies {
    modImplementation "maven.modrinth:config-manager:${project.configmanager_version}"
}
```
- Java
```java
import dev.felixagairu.configmanager.ConfigManager;

public class Test() {
	private static final String defaultConfigs ="{'key':'value'}";
	private static final ConfigManager configManager = new ConfigManager("modid.json", defaultConfigs);
	private static final JsonObject configs = configManager.loadConfig();
}
```

## Licenses
This library is released under the [MPL-2.0](LICENSE).

Partial parts of the project are based on [gson](https://github.com/google/gson) under [Apache-2.0](https://github.com/google/gson/blob/main/LICENSE).

[icon.png](https://github.com/FelixAgairu/config-manager/blob/4fd8f18df3d7a1828d7902e03d3bc05898de4553/src/main/resources/assets/config-manager/icon.png) © 2025 by [FelixAgairu](https://github.com/FelixAgairu) is licensed under [CC BY-NC-SA 4.0](https://creativecommons.org/licenses/by-nc-sa/4.0/).

[English](/README.md) **[简体中文](README-zh_cn.md)**

获取方式：

![Modrinth Downloads](https://img.shields.io/modrinth/dt/config-manager?logo=modrinth&label=Downloads%20%E4%B8%8B%E8%BD%BD%E9%87%8F&link=https%3A%2F%2Fmodrinth.com%2Fmod%2Fconfig-manager)

# 设置管理器
基于GSON的配置文件序列化器。

## 使用
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

## 许可证
该库根据 [MPL-2.0](LICENSE) 许可证发布。

该项目的部分内容基于 [gson](https://github.com/google/gson) 根据 [Apache-2.0](https://github.com/google/gson/blob/main/LICENSE)许可证发布。

[FelixAgairu](https://github.com/FelixAgairu) 的作品 [icon.png](https://github.com/FelixAgairu/config-manager/blob/4fd8f18df3d7a1828d7902e03d3bc05898de4553/src/main/resources/assets/config-manager/icon.png) © 2025 通过 [CC BY-NC-SA 4.0](https://creativecommons.org/licenses/by-nc-sa/4.0/) 协议授权。

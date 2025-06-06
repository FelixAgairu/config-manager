[English](/README.md) **[简体中文](README-zh_cn.md)**
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
import dev.felixagairu.configmanager.ConfigManager

public class Test() {
	private static final String defaultConfigs ="{'key':'value'}";
	private static final ConfigManager configManager = new ConfigManager("modid.json", defaultConfigs);
	private static final JsonObject configs = configManager.loadConfig();
}
```

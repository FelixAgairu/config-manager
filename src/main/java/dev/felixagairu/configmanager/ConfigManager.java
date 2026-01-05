/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.felixagairu.configmanager;

import com.google.gson.*;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

// Fabric use slf4j
// Fabric 加载器使用 slf4j 日志系统
/*? if fabric {*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.loader.api.FabricLoader;
/*?}*/

// Forge and NeoForge use log4j
// Forge 和 NeoForge 加载器使用 log4j 日志系统
/*? if forge || neoforge {*/
/*import java.nio.file.Path;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

// Forge →    net.minecraftforge.fml.loading.FMLPaths
// neoForge → net.neoforged.fml.loading.FMLPaths
import net./^? forge {^/ /^minecraftforge ^//^?} else if neoforge {^/ /^neoforged ^//^?}^/.fml.loading.FMLPaths;
*//*?}*/

public final class ConfigManager {
    public static final String MOD_ID = "config-manager";
    // Fabric →          Logger LOGGER = LoggerFactory.getLogger(MOD_ID)
    // Forge NeoForge →  Logger LOGGER = LogManager.getLogger(MOD_ID)
    private static final Logger LOGGER = /*? fabric {*/ LoggerFactory /*?} else if forge || neoforge {*/ /*LogManager *//*?}*/.getLogger(MOD_ID);

    private static final Map<String, JsonObject> DEFAULT_CONFIGS = new HashMap<>();
    private static final Map<String, JsonObject> CONFIGS = new HashMap<>();
    private static final Map<String, File> CONFIGS_FILES = new HashMap<>();

    // Create with setPrettyPrinting()
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    private ConfigManager() {}

    public static boolean setDefault(String modId, String defaultConfigs) {
        JsonObject defaultConfigsJson;
        // Set the default config content
        // 设置默认配置文件内容

        // Old version of Java Gson by using "new JsonParser()"
        // New version of Java Gson by using JsonParser's parseString()
        // 旧版 Java 的 Gson 使用 new JsonParser()
        // 新版 Java 的 Gson 使用 JsonParser 的 parseString()" 方法
        /*? if <1.19 {*/
        /*defaultConfigsJson = new JsonParser().parse(defaultConfigs).getAsJsonObject();
         *///?} else {
        defaultConfigsJson = JsonParser.parseString(defaultConfigs).getAsJsonObject();
        /*?}*/

        return DEFAULT_CONFIGS.put(modId, defaultConfigsJson) != null;
    }

    public static JsonObject getDefault(String modId) {
        // Old version of Java Gson (<2.8.2) has no deepCopy()
        // 旧版 Java Gson 没有 deepCopy() 方法
        /*? if <1.19 {*/
        /*return GSON.fromJson(DEFAULT_CONFIGS.get(modId), JsonObject.class);
        *//*?} else {*/
        return DEFAULT_CONFIGS.get(modId).deepCopy();
        /*?}*/
    }

    public static boolean setConfigsFile(String modId) {
        return setConfigsFile(modId, null);
    }
    public static boolean setConfigsFile(String modId, @Nullable String fileName) {
        final String name = (fileName == null) ? modId + ".json" : fileName;

        File configsFile;

        /*? if fabric {*/
            configsFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), name);
        /*?}*/

        /*? if forge || neoforge {*//*
            Path configPath = FMLPaths.CONFIGDIR.get();
            configsFile = configPath.resolve(name).toFile();
        *//*?}*/

        return CONFIGS_FILES.put(modId, configsFile) != null;
    }

    public static File getConfigFile(String modId) {
        return CONFIGS_FILES.get(modId);
    }

    /**
     *
     * @param modId The mod's name (or ID) <br>
     *              模组名称或 ID
     * @param config Json Object that contain the configs <br>
     *               包含设置内容的 Json 对象
     * @return True:<br>
     *         When save successful <br>
     *         保存成功时 <br>
     *         False:<br>
     *         When IOException, Can not delete or rename file or any other exceptions <br>
     *         有 I/O 异常、无法删除、重命名文件或其他类似异常
     */
    public static boolean setConfig(String modId, JsonObject config) {
        File temp = new File(CONFIGS_FILES.get(modId).getAbsolutePath() + ".tmp");
        String fileFullPath = CONFIGS_FILES.get(modId).getAbsolutePath();

        try (Writer writer = new FileWriter(temp)) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            // Log but do not throw
            LOGGER.error("Failed to write temp config file {} \n {}", fileFullPath, e.getStackTrace());
            return false;
        }

        // Try replacing the original file
        try {
            if (CONFIGS_FILES.get(modId).exists() && !CONFIGS_FILES.get(modId).delete()) {
                LOGGER.error("Failed to delete old config file {}", fileFullPath);
                return false;
            }

            if (!temp.renameTo(CONFIGS_FILES.get(modId))) {
                LOGGER.error("Failed to rename temp config file {}", fileFullPath);
                return false;
            }

            return true;

        } catch (Exception e) {
            LOGGER.error("Failed to replace config file {} \n {}", fileFullPath, e.getStackTrace());
            return false;
        }
    }

    /**
     *
     * @param modId The mod's name (or ID) <br>
     *              模组名称或 ID
     *
     * @return JsonObject contain configs <br>
     *         包含配置的 JsonObject 对象
     */
    public static JsonObject getConfigs(String modId) {
        File configsFile = getConfigFile(modId);

        // Try reading the config file normally
        // 尝试读取文件
        try (Reader reader = new FileReader(configsFile)) {
            JsonObject obj = GSON.fromJson(reader, JsonObject.class);

            // Null or non-object → treat as empty
            if (obj == null) {
                return usingDefaultConfig(modId);
            }

            return obj;

        } catch (JsonSyntaxException e) {
            // 1. Malformed JSON element → fallback to defaults
            // 损坏的 JSON 对象
            LOGGER.error("Failed to read malformed or broken config file {} \n {}", CONFIGS_FILES.get(modId).getAbsolutePath(), e.getStackTrace());
            return usingDefaultConfig(modId);
        } catch (FileNotFoundException e) {
            // 2. Config missing → regenerate and retry once
            // 文件不存在
            if (!resetConfig(modId)) {
                return safeReadAfterReset(modId);
            }

            // 3. If reset fails → fallback to defaults
            // 使用默认配置
            return usingDefaultConfig(modId);

        } catch (IOException e) {
            // 4. IO error → fallback to defaults
            // I/O 错误
            return usingDefaultConfig(modId);
        }
    }

    private static JsonObject safeReadAfterReset(String modId) {
        File configsFile = CONFIGS_FILES.get(modId);

        try (Reader reader = new FileReader(configsFile)) {
            JsonObject obj = GSON.fromJson(reader, JsonObject.class);
            return obj != null ? obj : getDefault(modId);
        } catch (Exception ignored) {
            return usingDefaultConfig(modId);
        }
    }

    private static JsonObject usingDefaultConfig(String modId) {
        // Warning when using default config
        // 使用默认配置时发出警告
        LOGGER.warn("Fallback to defaults config file: {}", CONFIGS_FILES.get(modId).getAbsolutePath());
        return getDefault(modId);
    }

    public static boolean resetConfig(String modId) {
        return setConfig(modId, getDefault(modId));
    }
}


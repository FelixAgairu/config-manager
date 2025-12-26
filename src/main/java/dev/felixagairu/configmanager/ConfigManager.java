/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.felixagairu.configmanager;

import com.google.gson.*;

import java.io.*;
import java.util.Locale;

// Fabric use slf4j, forge and neoforge use log4j
// 不同加载器使用不同日志系统

/*? if fabric {*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.loader.api.FabricLoader;
/*?}*/

/*? if forge || neoforge {*/
/*import java.nio.file.Path;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

// Forge →    net.minecraftforge.fml.loading.FMLPaths
// neoForge → net.neoforged.fml.loading.FMLPaths
import net./^? forge {^/ /^minecraftforge ^//^?} else if neoforge {^/ /^neoforged ^//^?}^/.fml.loading.FMLPaths;
*//*?}*/

public class ConfigManager {
    public final String MOD_ID = "config-manager";
    // Fabric →                  LoggerFactory.getLogger(MOD_ID)
    // Forge neoForge →          LogManager.getLogger(MOD_ID)
    public final Logger LOGGER = /*? fabric {*/ LoggerFactory /*?} else if forge || neoforge {*/ /*LogManager *//*?}*/.getLogger(MOD_ID);

    private final Gson GSON = new Gson();
    private final File CONFIG_FILE;
    private final JsonObject DEFAULT_CONFIGS;

    public ConfigManager(String configFileName, String configDefault) {
        // Set the default config content
        // 设置默认配置文件内容

        // Old version of Java GSON use "new"
        // New version of Java GSON parseString()
        /*? if <1.19 {*/
        /*DEFAULT_CONFIGS = new JsonParser().parse(configDefault).getAsJsonObject();
        *///?} else {
        DEFAULT_CONFIGS = JsonParser.parseString(configDefault).getAsJsonObject();
        /*?}*/


        // Load file dir from given "configFileName"
        // 从"configFileName"获取配置文件路径

        /*? if fabric {*/
        CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), configFileName);
        /*?}*/

        /*? if forge || neoforge {*/
        /*Path configPath = FMLPaths.CONFIGDIR.get();
        CONFIG_FILE = configPath.resolve(configFileName).toFile();
        *//*?}*/
    }

//    public JsonObject loadConfig() {
//        // Read config file
//        // 读取配置文件
//        try (Reader reader = new FileReader(CONFIG_FILE)) {
//            JsonObject jO = GSON.fromJson(reader, JsonObject.class);
//            if (jO != null) {
//                return jO;
//            } else {
//                throw new FileHandleException("Empty config file");
//            }
//        } catch (FileNotFoundException e) {
//            // Reset the config file
//            // 重置配置文件
//            if (resetConfig()) {
//                return loadConfig();
//            } else {
//                throw new FileHandleException("Failed to reset config: " + e.getMessage());
//            }
//        } catch (IOException e) {
//            throw new FileHandleException("Failed to load config: " + e.getMessage());
//        }
//    }
    public JsonObject loadConfig() {
        // Try reading the config file normally
        // 尝试读取文件
        try (Reader reader = new FileReader(CONFIG_FILE)) {
            JsonObject obj = GSON.fromJson(reader, JsonObject.class);

            // Null or non-object → treat as empty
            if (obj == null) {
                return usingDefaultConfig();
            }

            return obj;

        } catch (JsonSyntaxException e) {
            // 1. Malformed JSON element → fallback to defaults
            // 损坏的 JSON 对象
            LOGGER.error("Failed to read malformed or broken config file", e);
            return usingDefaultConfig();
        } catch (FileNotFoundException e) {
            // 2. Config missing → regenerate and retry once
            // 文件不存在
            if (resetConfig()) {
                return safeReadAfterReset();
            }

            // 3. If reset fails → fallback to defaults
            // 使用默认配置
            return usingDefaultConfig();

        } catch (IOException e) {
            // 4. IO error → fallback to defaults
            // I/O 错误
            return usingDefaultConfig();
        }
    }

    private JsonObject safeReadAfterReset() {
        try (Reader reader = new FileReader(CONFIG_FILE)) {
            JsonObject obj = GSON.fromJson(reader, JsonObject.class);
            return obj != null ? obj : defaultConfig();
        } catch (Exception ignored) {
            return usingDefaultConfig();
        }
    }

    private JsonObject usingDefaultConfig() {
        // Warning when using default config
        // 使用默认配置时发出警告
        LOGGER.warn("Fallback to defaults config file: {}", CONFIG_FILE.getName());
        return defaultConfig();
    }

    private JsonObject defaultConfig() {
        /*? if <1.19 {*/
        /*return GSON.fromJson(DEFAULT_CONFIGS, JsonObject.class);
         *///?} else {
        return DEFAULT_CONFIGS.deepCopy();
        /*?}*/
    }

//    public boolean saveConfig(JsonObject config) {
//        // Save config file
//        // 保存配置文件
//        try (Writer writer = new FileWriter(CONFIG_FILE)) {
//            GSON.toJson(config, writer);
//            return true;
//        } catch (IOException e) {
//            throw new FileHandleException("Failed to save config: " + e.getMessage());
//        }
//    }
    public boolean saveConfig(JsonObject config) {
        File temp = new File(CONFIG_FILE.getAbsolutePath() + ".tmp");

        try (Writer writer = new FileWriter(temp)) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            // Log but do not throw
            LOGGER.error("Failed to write temp config file", e);
            return false;
        }

        // Try replacing the original file
        try {
            if (CONFIG_FILE.exists() && !CONFIG_FILE.delete()) {
                LOGGER.error("Failed to delete old config file");
                return false;
            }

            if (!temp.renameTo(CONFIG_FILE)) {
                LOGGER.error("Failed to rename temp config file");
                return false;
            }

            return true;

        } catch (Exception e) {
            LOGGER.error("Failed to replace config file", e);
            return false;
        }
    }

    public boolean resetConfig() {
        return saveConfig(defaultConfig());
    }

    public static boolean parseBooleanSafe(JsonElement element) {
        if (element == null || element.isJsonNull()) return false;
        if (!element.isJsonPrimitive()) return false;

        JsonPrimitive p = element.getAsJsonPrimitive();

        if (p.isBoolean()) {
            return p.getAsBoolean();
        }

        if (p.isNumber()) {
            return p.getAsInt() != 0;
        }

        if (p.isString()) {
            String s = p.getAsString().trim().toLowerCase(Locale.ROOT);
            return s.equals("true") || s.equals("1") || s.equals("yes") || s.equals("on");
        }

        return false;
    }


    public static int parseIntegerSafe(JsonElement element) {
        if (element == null || element.isJsonNull()) return 0;
        if (!element.isJsonPrimitive()) return 0;

        JsonPrimitive p = element.getAsJsonPrimitive();

        if (p.isNumber()) {
            return p.getAsInt();
        }

        if (p.isString()) {
            try {
                return Integer.parseInt(p.getAsString().trim());
            } catch (NumberFormatException ignored) {}
        }

        return 0;
    }


    public static float parseFloatSafe(JsonElement element) {
        if (element == null || element.isJsonNull()) return 0f;
        if (!element.isJsonPrimitive()) return 0f;

        JsonPrimitive p = element.getAsJsonPrimitive();

        if (p.isNumber()) {
            return p.getAsFloat();
        }

        if (p.isString()) {
            try {
                return Float.parseFloat(p.getAsString().trim());
            } catch (NumberFormatException ignored) {}
        }

        return 0f;
    }


    public static String parseStringSafe(JsonElement element) {
        if (element == null || element.isJsonNull()) return "";
        if (!element.isJsonPrimitive()) return "";

        try {
            return element.getAsString();
        } catch (Exception ignored) {
            return "";
        }
    }

}


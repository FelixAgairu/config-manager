/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.felixagairu.configmanager;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;

public class ConfigManager {
    private final Gson GSON = new Gson();
    private final File CONFIG_FILE;
    private final JsonObject DEFAULT_CONFIGS;

    public ConfigManager(String configFileName, String configDefault) {
        // Load file dir from given "configFileName"
        // 从"configFileName"获取配置文件路径
        CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), configFileName);
        // Set the default config content
        // 设置默认配置文件内容
        DEFAULT_CONFIGS = JsonParser.parseString(configDefault).getAsJsonObject();
    }

    public JsonObject loadConfig() {
        // Read config file
        // 读取配置文件
        try (Reader reader = new FileReader(CONFIG_FILE)) {
            JsonObject jO = GSON.fromJson(reader, JsonObject.class);
            if (jO != null) {
                return jO;
            } else {
                System.err.println("[config-manager] Failed to load config: Might empty file!");
                return DEFAULT_CONFIGS;
            }
        } catch (IOException e) {
            System.err.println("[config-manager] Failed to load config: " + e.getMessage());
            return DEFAULT_CONFIGS;
        }
    }

    public boolean saveConfig(JsonObject config) {
        // Save config file
        // 保存配置文件
        try (Writer writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(config, writer);
            return true;
        } catch (IOException e) {
            System.err.println("[config-manager] Failed to save config: " + e.getMessage());
            return false;
        }
    }

    public boolean resetConfig() {
        return saveConfig(DEFAULT_CONFIGS);
    }
    /*
    public static void registerShutdownHandler() {
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            // Save your configuration file here
            saveConfig();
        });
    }
 */
}


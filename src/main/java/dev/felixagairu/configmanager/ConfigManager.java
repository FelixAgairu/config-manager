/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.felixagairu.configmanager;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;

import dev.felixagairu.configmanager.exceptions.*;

/*? if fabric {*/
import net.fabricmc.loader.api.FabricLoader;
/*?}*/

/*? if forge || neoforge {*/
/*import net./^? forge {^/ /^minecraftforge ^//^?} else if neoforge {^/ neoforged /^?}^/.fml.loading.FMLPaths;
import java.nio.file.Path;
*//*?}*/

public class ConfigManager {
    private final Gson GSON = new Gson();
    private final File CONFIG_FILE;
    private final JsonObject DEFAULT_CONFIGS;

    public ConfigManager(String configFileName, String configDefault) {
        // Set the default config content
        // 设置默认配置文件内容
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

    public JsonObject loadConfig() {
        // Read config file
        // 读取配置文件
        try (Reader reader = new FileReader(CONFIG_FILE)) {
            JsonObject jO = GSON.fromJson(reader, JsonObject.class);
            if (jO != null) {
                return jO;
            } else {
                throw new FileHandleException("Empty config file");
            }
        } catch (FileNotFoundException e) {
            // Reset the config file
            // 重置配置文件
            if (resetConfig()) {
                return loadConfig();
            } else {
                throw new FileHandleException("Failed to reset config: " + e.getMessage());
            }
        } catch (IOException e) {
            throw new FileHandleException("Failed to load config: " + e.getMessage());
        }
    }

    public boolean saveConfig(JsonObject config) {
        // Save config file
        // 保存配置文件
        try (Writer writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(config, writer);
            return true;
        } catch (IOException e) {
            throw new FileHandleException("Failed to save config: " + e.getMessage());
        }
    }

    public boolean resetConfig() {
        return saveConfig(DEFAULT_CONFIGS);
    }
}


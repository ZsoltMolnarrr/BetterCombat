package net.tinyconfig;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
/* https://github.com/ZsoltMolnarrr/TinyConfig/blob/main/src/main/java/net/tinyconfig/ConfigManager.java
 *
 * The MIT License (MIT)
 * Copyright (c) 2022
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

public class ConfigManager<Config> {
    static final Logger LOGGER = LogUtils.getLogger();

    public Config currentConfig;
    public String configName;
    public String directory;
    public boolean isLoggingEnabled = false;
    public boolean sanitize = false;

    public ConfigManager(String configName, Config defaultConfig) {
        this.configName = configName;
        this.currentConfig = defaultConfig;
    }

    public void refresh() {
        var filePath = getConfigFilePath();
        load();
        if (sanitize || !Files.exists(filePath)) {
            save();
        }
    }

    public void load() {
        var filePath = getConfigFilePath();

        try {
            var gson = new Gson();
            if (Files.exists(filePath)) {
                // Read
                Reader reader = Files.newBufferedReader(filePath);
                currentConfig = (Config) gson.fromJson(reader, currentConfig.getClass());
                reader.close();
            }
        } catch (IOException e) {
            if (isLoggingEnabled) {
                LOGGER.error("Failed loading " + configName + " config: " + e.getMessage());
            }
        }
    }

    public void save() {
        var config = currentConfig;
        var filePath = getConfigFilePath();
        Path configDir = getConfigDir();

        try {
            if (directory != null && !directory.isEmpty()) {
                var directoryPath = configDir.resolve(directory);
                Files.createDirectories(directoryPath);
            }
            var prettyGson = new GsonBuilder().setPrettyPrinting().create();
            Writer writer = Files.newBufferedWriter(filePath);
            writer.write(prettyGson.toJson(config));
            writer.close();
            if (isLoggingEnabled) {
                var gson = new Gson();
                LOGGER.info(configName + " config written: " + gson.toJson(config));
            }
        } catch(Exception e) {
            if (isLoggingEnabled) {
                LOGGER.error("Failed writing " + configName + " config: " + e.getMessage());
            }
        }
    }

    private Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get(); // FabricLoader.getInstance().getConfigDir();
    }

    private Path getConfigFilePath() {
        var configFilePath = configName + ".json";
        if (directory != null && !directory.isEmpty()) {
            configFilePath = directory + "/" + configFilePath;
        }
        Path configDir = getConfigDir();
        return configDir.resolve(configFilePath);
    }

    public Builder builder() {
        return new Builder(this);
    }

    public class Builder {
        ConfigManager<Config> manager;
        Builder(ConfigManager<Config> manager) {
            this.manager = manager;
        }

        public Builder enableLogging(boolean enable) {
            manager.isLoggingEnabled = enable;
            return this;
        }

        public Builder setDirectory(String directory) {
            manager.directory = directory;
            return this;
        }

        public Builder sanitize(boolean sanitize) {
            manager.sanitize = sanitize;
            return this;
        }

        public ConfigManager<Config> build() {
            return manager;
        }
    }
}
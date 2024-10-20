package ru.fiarr4ik.oceanblockseller.service;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

    public class ConfigService {

        private final FileConfiguration config;
        private final File itemConfig;

        public ConfigService(JavaPlugin plugin) {
            File configFile = new File(plugin.getDataFolder(), "config/config.yaml");

            if (!configFile.exists()) {
                try {
                    configFile.getParentFile().mkdirs();

                    InputStream configStream = plugin.getResource("config/config.yaml");
                    if (configStream != null) {
                        Files.copy(configStream, configFile.toPath());
                        plugin.getLogger().info("Файл config.yaml скопирован из ресурсов.");
                    } else {
                        plugin.getLogger().warning("Не удалось найти config.yaml в ресурсах плагина.");
                    }
                } catch (IOException e) {
                    plugin.getLogger().severe("Ошибка при создании файла config.yaml: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            this.config = YamlConfiguration.loadConfiguration(configFile);
            this.itemConfig = new File(plugin.getDataFolder(), "config/items.json");
        }

        public FileConfiguration getConfig() {
            return config;
        }

        public File getItemConfig() {
            return itemConfig;
        }
    }

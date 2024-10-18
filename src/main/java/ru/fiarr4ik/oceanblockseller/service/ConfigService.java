package ru.fiarr4ik.oceanblockseller.service;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

    public class ConfigService {

        private final FileConfiguration config;

        public ConfigService(JavaPlugin plugin) {
            File configFile = new File(plugin.getDataFolder(), "config/config.yaml");
            this.config = YamlConfiguration.loadConfiguration(configFile);
        }

        public FileConfiguration getConfig() {
            return config;
        }

    }

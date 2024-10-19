package ru.fiarr4ik.oceanblockseller.service;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

    public class EconomyService {
        private final JavaPlugin plugin;
        private Economy economy = null;

        public EconomyService(JavaPlugin plugin) {
            this.plugin = plugin;
            setupEconomy();
        }

        private boolean setupEconomy() {
            if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
                plugin.getLogger().severe("Vault плагин не найден! Отключение плагина...");
                return false;
            }
            RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp == null) {
                plugin.getLogger().severe("Сервис экономики не зарегистрирован в Vault! Отключение плагина...");
                return false;
            }
            economy = rsp.getProvider();
            return economy != null;
        }

        public Economy getEconomy() {
            return economy;
        }
    }

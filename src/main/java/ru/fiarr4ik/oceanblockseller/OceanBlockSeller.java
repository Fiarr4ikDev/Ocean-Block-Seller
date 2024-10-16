package ru.fiarr4ik.oceanblockseller;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import ru.fiarr4ik.oceanblockseller.command.ReloadTradesCommand;
import ru.fiarr4ik.oceanblockseller.command.SellerCommand;
import ru.fiarr4ik.oceanblockseller.command.SellerTabCompleter;

import java.io.File;
import java.time.LocalTime;

import static ru.fiarr4ik.oceanblockseller.utils.UtilityClass.getSellerInventory;
import static ru.fiarr4ik.oceanblockseller.utils.UtilityClass.loadTrades;
import static ru.fiarr4ik.oceanblockseller.utils.UtilityClass.setItemStackName;

    public final class OceanBlockSeller extends JavaPlugin implements Listener {

        private static OceanBlockSeller plugin;
        private static Economy econ = null;
        private static LocalTime time;
        private static FileConfiguration config;

        /**
         * Инициализация обьектов при запуске плагина.
         */
        @Override
        public void onEnable() {
            getLogger().info("OceanBlockSeller запущен (удивительно)");
            //inits
            plugin = this;
            File configFile = new File(plugin.getDataFolder(), "config/config.yaml");
            File itemsFile = new File(plugin.getDataFolder(), "config/items.json");
            config = YamlConfiguration.loadConfiguration(configFile);
            time = LocalTime.of(config.getInt("timer.h"), config.getInt("timer.m"), config.getInt("timer.s"));

            setupEconomy(); //Установка экономики
            setupSellerInventory(); //pre-init инвентаря

            getCommand("seller").setExecutor(new SellerCommand(this));
            getCommand("seller").setTabCompleter(new SellerTabCompleter());
            getCommand("reloadsell").setExecutor(new ReloadTradesCommand(this));
            getServer().getPluginManager().registerEvents(new SellerCommand(this), this);
            startTimer();
            Player player = Bukkit.getPlayer(getServer().getConsoleSender().getName());
            loadTrades(player, itemsFile);
        }

        private void startTimer() {
            Bukkit.getScheduler().runTaskTimer(this, this::updateTime, 0L, 20L);
        }

        private void updateTime() {
            ItemStack timer = new ItemStack(Material.CLOCK, 1);
            setItemStackName(timer,
                    ChatColor.AQUA + "Время до обновления таймера " +
                            ChatColor.GOLD + OceanBlockSeller.getTime().toString());
            getSellerInventory().setItem(51, timer);

            if (time.getSecond() > 0 || time.getMinute() > 0 || time.getHour() > 0) {
                time = time.minusSeconds(1);
            } else {

                for (Player p : Bukkit.getOnlinePlayers()) {
                    File file = new File(plugin.getDataFolder(), "config/items.json");
                    loadTrades(p, file);
                }
                time = LocalTime.of(config.getInt("timer.h"), config.getInt("timer.m"), config.getInt("timer.s"));
            }
        }

        private void setupSellerInventory() {
            getSellerInventory();
        }

        private boolean setupEconomy() {
            if (getServer().getPluginManager().getPlugin("Vault") == null) {
                getLogger().severe("Vault плагин не найден! Отключение плагина...");
                return false;
            }
            RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp == null) {
                getLogger().severe("Сервис экономики не зарегистрирован в Vault! Отключение плагина...");
                return false;
            }
            econ = rsp.getProvider();
            return econ != null;
        }

        @Override
        public void onDisable() {

        }

        public static Economy getEconomy() {
            return econ;
        }

        public static LocalTime getTime() {
            return time;
        }

        public FileConfiguration getConfig() {
            return config;
        }
    }

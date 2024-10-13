package ru.fiarr4ik.oceanblockseller;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
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
        private static Permission perms = null;
        private static Chat chat = null;
        private static LocalTime time = LocalTime.of(4, 0, 0);
        private static File configFile = new File(plugin.getDataFolder(), "config/config.yaml");
        private static FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        @Override
        public void onEnable() {
            setupEconomy();
            setupSellerInventory();

            getCommand("seller").setExecutor(new SellerCommand(this));
            getCommand("seller").setTabCompleter(new SellerTabCompleter());
            getCommand("reloadsell").setExecutor(new ReloadTradesCommand(this));
            getServer().getPluginManager().registerEvents(new SellerCommand(this), this);
            startTimer();
            Player player = Bukkit.getPlayer(getServer().getConsoleSender().getName());
            File file = new File(plugin.getDataFolder(), "config/items.json");
            loadTrades(player, file);
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
                    Location loc = p.getLocation();
                    p.playSound(loc, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 2, 2);
                }
                time = LocalTime.of(4, 0, 0);
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

        private boolean setupChat() {
            RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
            if (rsp == null) {
                getLogger().warning("Chat service provider is not registered!");
                return false;
            }

            chat = rsp.getProvider();
            if (chat == null) {
                getLogger().warning("Chat service provider is null!");
            }

            return chat != null;
        }

        private boolean setupPermissions() {
            RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
            perms = rsp.getProvider();
            return perms != null;
        }

        @Override
        public void onDisable() {

        }

        public static Economy getEconomy() {
            return econ;
        }

        public static Permission getPermissions() {
            return perms;
        }

        public static LocalTime getTime() {
            return time;
        }

        public static File getConfigFile() {
            return configFile;
        }

        public FileConfiguration getConfig() {
            return config;
        }
    }

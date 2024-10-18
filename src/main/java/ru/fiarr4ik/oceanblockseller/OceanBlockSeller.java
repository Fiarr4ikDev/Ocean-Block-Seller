package ru.fiarr4ik.oceanblockseller;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import ru.fiarr4ik.oceanblockseller.command.ReloadTradesCommand;
import ru.fiarr4ik.oceanblockseller.command.SellerCommand;
import ru.fiarr4ik.oceanblockseller.command.SellerTabCompleter;
import ru.fiarr4ik.oceanblockseller.service.ConfigService;
import ru.fiarr4ik.oceanblockseller.utils.UtilityClass;

import java.io.File;
import java.time.LocalTime;

import static ru.fiarr4ik.oceanblockseller.utils.UtilityClass.setItemStackName;

    public final class OceanBlockSeller extends JavaPlugin implements Listener {

        private static OceanBlockSeller plugin;
        private static Economy econ = null;
        private static LocalTime time;
        private final ConfigService configService;
        private final UtilityClass utilityClass;

        public OceanBlockSeller() {
            this.configService = new ConfigService(this);
            this.utilityClass = new UtilityClass(this);
        }

        /**
         * Инициализация обьектов при запуске плагина.
         */
        @Override
        public void onEnable() {
            getLogger().info("OceanBlockSeller запущен (удивительно)");
            //inits
            plugin = this;
            File itemsFile = new File(plugin.getDataFolder(), "config/items.json");
            time = LocalTime.of(
                    configService.getConfig().getInt("timer.h"),
                    configService.getConfig().getInt("timer.m"),
                    configService.getConfig().getInt("timer.s"));

            setupEconomy(); //Установка экономики
            setupSellerInventory(); //pre-init инвентаря

            getCommand("seller").setExecutor(new SellerCommand(this));
            getCommand("seller").setTabCompleter(new SellerTabCompleter());
            getCommand("reloadsell").setExecutor(new ReloadTradesCommand(this));
            getServer().getPluginManager().registerEvents(new SellerCommand(this), this);
            startTimer();
            Player player = Bukkit.getPlayer(getServer().getConsoleSender().getName());
            utilityClass.loadTrades(player, itemsFile);
        }

        private void startTimer() {
            Bukkit.getScheduler().runTaskTimer(this, this::updateTime, 0L, 20L);
        }

        private void updateTime() {
            ItemStack timer = new ItemStack(Material.CLOCK, 1);
            setItemStackName(timer, configService.getConfig().getString("messages.timeToUpdateTimer") + " " +
                            ChatColor.GOLD + OceanBlockSeller.getTime().toString());

            utilityClass.getSellerInventory().setItem(51, timer);

            if (time.getSecond() > 0 || time.getMinute() > 0 || time.getHour() > 0) {
                time = time.minusSeconds(1);
            } else {

                for (Player p : Bukkit.getOnlinePlayers()) {
                    File file = new File(plugin.getDataFolder(), "config/items.json");
                    utilityClass.loadTrades(p, file);
                }
                time = LocalTime.of(
                        configService.getConfig().getInt("timer.h"),
                        configService.getConfig().getInt("timer.m"),
                        configService.getConfig().getInt("timer.s"));
            }
        }

        private void setupSellerInventory() {
            utilityClass.getSellerInventory();
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

    }

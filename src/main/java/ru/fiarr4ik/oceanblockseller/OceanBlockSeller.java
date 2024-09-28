package ru.fiarr4ik.oceanblockseller;

import lombok.Getter;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import ru.fiarr4ik.oceanblockseller.command.ReloadTradesCommand;
import ru.fiarr4ik.oceanblockseller.command.SellerCommand;
import ru.fiarr4ik.oceanblockseller.utils.UtilityClass;

import java.io.File;
import java.time.LocalTime;

import static ru.fiarr4ik.oceanblockseller.utils.UtilityClass.getSellerInventory;
import static ru.fiarr4ik.oceanblockseller.utils.UtilityClass.setItemStackName;

    public final class OceanBlockSeller extends JavaPlugin implements Listener {

        private final JavaPlugin plugin = this;
        private static Economy econ = null;
        private static Permission perms = null;
        private static Chat chat = null;
        @Getter
        private static LocalTime time = LocalTime.of(0, 0, 20);

        @Override
        public void onEnable() {
            setupEconomy();
            setupSellerInventory();

            getCommand("seller").setExecutor(new SellerCommand(this));
            getCommand("reloadsell").setExecutor(new ReloadTradesCommand(this));
            getServer().getPluginManager().registerEvents(new SellerCommand(this), this);
            startTimer();
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
                    UtilityClass.loadTrades(p, file);
                    Location loc = p.getLocation();
                    p.playSound(loc, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 2, 2);
                }
                time = LocalTime.of(0, 0, 20);
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

    }

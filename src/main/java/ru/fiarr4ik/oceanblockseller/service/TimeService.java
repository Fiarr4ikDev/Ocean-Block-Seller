package ru.fiarr4ik.oceanblockseller.service;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.fiarr4ik.oceanblockseller.OceanBlockSeller;

import java.io.File;
import java.time.LocalTime;

    public class TimeService {
        private LocalTime time;
        private final OceanBlockSeller plugin;
        private final InventoryService inventoryService;
        private final TradeService tradeService;
        private final ConfigService configService;
        private final File itemConfig;

        public TimeService(OceanBlockSeller plugin) {
            this.plugin = plugin;
            this.inventoryService = new InventoryService(plugin);
            this.tradeService = new TradeService(plugin);
            this.configService = new ConfigService(plugin);
            this.itemConfig = configService.getItemConfig();

            time = LocalTime.of(
                    configService.getConfig().getInt("timer.h"),
                    configService.getConfig().getInt("timer.m"),
                    configService.getConfig().getInt("timer.s"));
        }

        public void startTimer() {
            Bukkit.getScheduler().runTaskTimer(plugin, this::updateTime, 0L, 20L);
        }

        private void updateTime() {
            ItemStack timer = new ItemStack(Material.CLOCK, 1);
            inventoryService.setItemStackName(timer, configService.getConfig().getString("messages.timeToUpdateTimer") + " " +
                    ChatColor.GOLD + time.toString());

            inventoryService.getSellerInventory().setItem(51, timer);

            if (time.getSecond() > 0 || time.getMinute() > 0 || time.getHour() > 0) {
                time = time.minusSeconds(1);
            } else {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    tradeService.loadTrades(p, itemConfig);
                }
                resetTimer();
            }
        }

        private void resetTimer() {
            time = LocalTime.of(
                    configService.getConfig().getInt("timer.h"),
                    configService.getConfig().getInt("timer.m"),
                    configService.getConfig().getInt("timer.s"));
        }
    }


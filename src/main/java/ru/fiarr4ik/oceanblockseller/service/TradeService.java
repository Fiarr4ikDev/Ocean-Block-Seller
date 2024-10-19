package ru.fiarr4ik.oceanblockseller.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import ru.fiarr4ik.oceanblockseller.dto.Item;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static ru.fiarr4ik.oceanblockseller.command.SellerCommand.addItemToInventory;
import static ru.fiarr4ik.oceanblockseller.command.SellerCommand.getRandomPrice;

    public class TradeService {

        private final ConfigService configService;
        private final InventoryService inventoryService;

        public TradeService(JavaPlugin plugin) {
            this.configService = new ConfigService(plugin);
            this.inventoryService = new InventoryService(plugin);
        }

        public void loadTrades(Player player, File file) {
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                List<Item> items = objectMapper.readValue(file, new TypeReference<List<Item>>() {});
                if (items.size() > 28) {
                    player.sendMessage(configService.getConfig().getString(configService.getConfig().getString("itemsByConfigMoreMaxInventory")));
                } else {
                    inventoryService.clearInventory();
                    Inventory inventory = inventoryService.getSellerInventory();

                    for (Item item : items) {
                        Material material = Material.getMaterial(item.getName().toUpperCase());
                        assert material != null;
                        ItemStack itemStack = new ItemStack(material);
                        addItemToInventory(inventory, itemStack, item.getAmount(), getRandomPrice(item.getMinPrice(), item.getMaxPrice()));
                    }

                    for (Player players : Bukkit.getOnlinePlayers()) {
                        players.sendTitle(
                                configService.getConfig().getString("messages.sellerReloadTitle"),
                                configService.getConfig().getString("messages.sellerReloadSubTitle"),
                                10, 70, 20);
                        Location location = players.getLocation();
                        players.playSound(location, Sound.ENTITY_CAT_STRAY_AMBIENT, 1, 1);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

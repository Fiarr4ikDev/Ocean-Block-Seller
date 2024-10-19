package ru.fiarr4ik.oceanblockseller.service;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class InventoryService {

        private static Inventory sharedSellerInventory;
        private final ConfigService configService;

        public InventoryService(JavaPlugin plugin) {
            this.configService = new ConfigService(plugin);
        }

        public Inventory getSellerInventory() {
            if (sharedSellerInventory == null) {
                sharedSellerInventory = Bukkit.createInventory(null, 54, "Скупщик");

                String mainItemName = Objects.requireNonNull(configService.getConfig().getString("gui.mainItem"), "mainItem не найден в конфигурации");
                Material mainMaterial = getGlassMaterial(mainItemName);
                if (mainMaterial == null) {
                    throw new IllegalArgumentException("Неправильный материал для mainItem: " + mainItemName);
                }

                String exitButtonName = Objects.requireNonNull(configService.getConfig().getString("gui.exitButtonItem"), "exitButtonItem не найден в конфигурации");
                Material exitButtonMaterial = getGlassMaterial(exitButtonName);
                if (exitButtonMaterial == null) {
                    throw new IllegalArgumentException("Неправильный материал для exitButtonItem: " + exitButtonName);
                }

                ItemStack exitButton = new ItemStack(exitButtonMaterial, 1);
                setItemStackName(exitButton, configService.getConfig().getString("messages.closeButton"));
                sharedSellerInventory.setItem(49, exitButton);

                ItemStack mainItem = new ItemStack(mainMaterial, 1);
                setItemStackName(mainItem, " ");
                for (int i = 0; i <= 8; i++) {
                    sharedSellerInventory.setItem(i, mainItem);
                }
                int[] blackGlassSlots = new int[]{9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 48, 50, 52, 53};
                for (Integer i : blackGlassSlots) {
                    sharedSellerInventory.setItem(i, mainItem);
                }

                ItemStack info = new ItemStack(Material.OAK_SIGN, 1);
                setItemStackName(info, configService.getConfig().getString("messages.sellerDescription"));
                sharedSellerInventory.setItem(47, info);
            }

            return sharedSellerInventory;
        }

        public void clearInventory() {
            for (int i = 10; i <= 16; i++) {
                sharedSellerInventory.setItem(i, null);
            }
            for (int i = 19; i <= 25; i++) {
                sharedSellerInventory.setItem(i, null);
            }
            for (int i = 28; i <= 34; i++) {
                sharedSellerInventory.setItem(i, null);
            }
            for (int i = 37; i <= 43; i++) {
                sharedSellerInventory.setItem(i, null);
            }
        }

        public void setItemStackName(ItemStack item, String name) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(name);
                item.setItemMeta(meta);
            }
        }

        private static Material getGlassMaterial(String name) {

            switch (name) {
                case "red_glass":
                    return Material.RED_STAINED_GLASS_PANE;
                case "orange_glass":
                    return Material.ORANGE_STAINED_GLASS_PANE;
                case "yellow_glass":
                    return Material.YELLOW_STAINED_GLASS_PANE;
                case "lime_glass":
                    return Material.LIME_STAINED_GLASS_PANE;
                case "green_glass":
                    return Material.GREEN_STAINED_GLASS_PANE;
                case "cyan_glass":
                    return Material.CYAN_STAINED_GLASS_PANE;
                case "light_blue_glass":
                    return Material.LIGHT_BLUE_STAINED_GLASS_PANE;
                case "blue_glass":
                    return Material.BLUE_STAINED_GLASS_PANE;
                case "purple_glass":
                    return Material.PURPLE_STAINED_GLASS_PANE;
                case "magenta_glass":
                    return Material.MAGENTA_STAINED_GLASS_PANE;
                case "pink_glass":
                    return Material.PINK_STAINED_GLASS_PANE;
                case "white_glass":
                    return Material.WHITE_STAINED_GLASS_PANE;
                case "gray_glass":
                    return Material.GRAY_STAINED_GLASS_PANE;
                case "light_gray_glass":
                    return Material.LIGHT_GRAY_STAINED_GLASS_PANE;
                case "black_glass":
                    return Material.BLACK_STAINED_GLASS_PANE;
                default:
                    return null;
            }
        }

    }

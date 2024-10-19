package ru.fiarr4ik.oceanblockseller.service;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

    public class InventoryService {

        private static Inventory sharedSellerInventory;
        private final ConfigService configService;

        public InventoryService(JavaPlugin plugin) {
            this.configService = new ConfigService(plugin);
        }

        public Inventory getSellerInventory() {
            if (sharedSellerInventory == null) {
                sharedSellerInventory = Bukkit.createInventory(null, 54, "Скупщик");

                ItemStack redGlass = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
                setItemStackName(redGlass, configService.getConfig().getString("messages.closeButton"));
                sharedSellerInventory.setItem(49, redGlass);

                ItemStack blackGlass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
                setItemStackName(blackGlass, " ");
                for (int i = 0; i <= 8; i++) {
                    sharedSellerInventory.setItem(i, blackGlass);
                }
                int[] blackGlassSlots = new int[] {9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 48, 50, 52, 53};
                for (Integer i : blackGlassSlots) {
                    sharedSellerInventory.setItem(i, blackGlass);
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

    }

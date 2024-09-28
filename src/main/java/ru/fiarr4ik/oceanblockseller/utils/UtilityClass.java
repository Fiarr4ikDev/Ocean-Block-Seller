package ru.fiarr4ik.oceanblockseller.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.fiarr4ik.oceanblockseller.config.Item;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static ru.fiarr4ik.oceanblockseller.command.SellerCommand.addItemToInventory;
import static ru.fiarr4ik.oceanblockseller.command.SellerCommand.getRandomPrice;

    public class UtilityClass {

        private static Inventory sharedSellerInventory;
        private static final String SERVER_PLUGIN_NAME = ChatColor.AQUA + "OceanSeller | ";

        /**
         * <a href="https://imgur.com/a/NwXtQ9K">Внешний вид продавца</a>
         */
        public static Inventory getSellerInventory() {

            if (sharedSellerInventory == null) {
                sharedSellerInventory = Bukkit.createInventory(null, 54, "Продавец");

                ItemStack redGlass = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
                setItemStackName(redGlass, "§cЗакрыть");

                ItemStack blackGlass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
                setItemStackName(blackGlass, " ");

                ItemStack info = new ItemStack(Material.OAK_SIGN, 1);
                setItemStackName(info, ChatColor.AQUA + "");

                for (int i = 0; i <= 8; i++) {
                    sharedSellerInventory.setItem(i, blackGlass);
                }
                sharedSellerInventory.setItem(9, blackGlass);
                sharedSellerInventory.setItem(17, blackGlass);
                sharedSellerInventory.setItem(18, blackGlass);
                sharedSellerInventory.setItem(26, blackGlass);
                sharedSellerInventory.setItem(27, blackGlass);
                sharedSellerInventory.setItem(35, blackGlass);
                sharedSellerInventory.setItem(36, blackGlass);
                sharedSellerInventory.setItem(44, blackGlass);
                sharedSellerInventory.setItem(45, blackGlass);
                sharedSellerInventory.setItem(46, blackGlass);
                sharedSellerInventory.setItem(47, info);
                sharedSellerInventory.setItem(48, blackGlass);
                sharedSellerInventory.setItem(49, redGlass);
                sharedSellerInventory.setItem(50, blackGlass);
                sharedSellerInventory.setItem(52, blackGlass);
                sharedSellerInventory.setItem(53, blackGlass);

            }

            return sharedSellerInventory;
        }

        /**
         * Очистка инвентаря торговца
         */
        public static void clearInventory() {
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

        /**
         * Устанавливает имя для предмета.
         *
         * @param item Предмет.
         * @param name Имя, которое нужно установить.
         */
        public static void setItemStackName(ItemStack item, String name) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(name);
                item.setItemMeta(meta);
            }
        }

        /**
         * Загрузка торгов по конфигу
         * @param player игрок
         * @param file - путь до файла с конфигом
         */
        public static void loadTrades(Player player, File file) {
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                List<Item> items = objectMapper.readValue(file, new TypeReference<List<Item>>() {});
                if (items.size() > 28) {
                    player.sendMessage(SERVER_PLUGIN_NAME + ChatColor.RED + " Количество предметов в конфиге больше вместительности скупщика. (28)");
                } else {
                    clearInventory();
                    Inventory inventory = getSellerInventory();

                    for (Item item : items) {
                        Material material = Material.getMaterial(item.getName().toUpperCase());
                        ItemStack itemStack = new ItemStack(material);
                        if (material != null) {

                            addItemToInventory(inventory, itemStack, item.getAmount(), getRandomPrice(item.getMinPrice(), item.getMaxPrice()));
                        }
                    }
                    for (Player players : Bukkit.getOnlinePlayers()) {
                        Location location = players.getLocation();
                        players.playSound(location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 2, 2);
                        player.sendMessage(SERVER_PLUGIN_NAME + ChatColor.WHITE + "Скупщик обновил свои товары и лимиты");
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

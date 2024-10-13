package ru.fiarr4ik.oceanblockseller.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import ru.fiarr4ik.oceanblockseller.config.Item;
import ru.fiarr4ik.oceanblockseller.utils.Chat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static ru.fiarr4ik.oceanblockseller.OceanBlockSeller.getEconomy;
import static ru.fiarr4ik.oceanblockseller.utils.UtilityClass.getSellerInventory;

    /**
     * Обработчик команд и слушатель событий для команды продавца и взаимодействия с инвентарём.
     */
    public class SellerCommand implements CommandExecutor, Listener {

        private final Economy economy;
        private final JavaPlugin plugin;
        private final ObjectMapper objectMapper;

        /**
         * Конструктор для инициализации экземпляра экономики.
         *
         * @param plugin Экземпляр плагина, используемый для инициализации экономики.
         */
        public SellerCommand(JavaPlugin plugin) {
            this.economy = getEconomy();
            this.plugin = plugin;
            this.objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        }

        /**
         * Обрабатывает команду '/seller' для открытия инвентаря продавца или добавления предметов в него.
         *
         * @param sender  Отправитель команды.
         * @param command Команда, которая выполняется.
         * @param label   Псевдоним команды, которая выполняется.
         * @param args    Аргументы, переданные с командой.
         * @return true, если команда успешно выполнена, иначе false.
         */
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            File configFile = new File(plugin.getDataFolder(), "config/config.yaml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (command.getName().equalsIgnoreCase("seller")) {
                    if (args.length == 0) {
                        if (player.hasPermission("itembuyer.seller")) {
                            player.openInventory(getSellerInventory());
                            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1, 1);
                        } else {
                            Chat.sendMessage(player, config.getString("messages.noPermission"));
                        }
                    } else if (args.length == 5 && args[0].equalsIgnoreCase("sell")) {
                        if (player.hasPermission("itembuyer.selleredit")) {
                            try {
                                int limit = Integer.parseInt(args[1]);
                                double minPrice = Double.parseDouble(args[2]);
                                double maxPrice = Double.parseDouble(args[3]);

                                Material material = Material.getMaterial(args[4].toUpperCase());
                                if (material == null) {
                                    Chat.sendMessage(player, config.getString("") + args[4]);
                                    return true;
                                }
                                if (limit <= 0) {
                                    Chat.sendMessage(player, config.getString("sellLimitError"));
                                } else {
                                    if (minPrice < 0.1 || maxPrice < 0.1) {
                                        Chat.sendMessage(player, config.getString("priceError"));
                                    } else {
                                        if (minPrice > maxPrice) {
                                            Chat.sendMessage(player, config.getString("minPriceGreater"));
                                        } else {
                                            ItemStack itemId = new ItemStack(material);

                                            if (itemId.getType() != Material.AIR) {
                                                double price = getRandomPrice(minPrice, maxPrice);

                                                Inventory inventory = getSellerInventory();
                                                addItemToInventory(inventory, itemId, limit, price);
                                                Chat.sendMessage(player, config.getString("sellItemAdded") + price + config.getString("sellItemLimit") + limit);

                                                saveItemToConfig(material.name().toLowerCase(), limit, minPrice, maxPrice);
                                            }
                                        }
                                    }
                                }
                            } catch (NumberFormatException e) {
                                Chat.sendMessage(player, config.getString("priceMustBeNumbers"));
                            }
                        } else {
                            Chat.sendMessage(player, config.getString("noPermission"));
                        }
                    } else {
                        Chat.sendMessage(player, config.getString("commandPrintError"));
                    }
                }
            }
            return true;
        }

        /**
         * Генерирует случайную цену в пределах заданного диапазона.
         *
         * @param min Минимальная цена.
         * @param max Максимальная цена.
         * @return Случайная цена, округленная до двух знаков после запятой.
         */
        public static double getRandomPrice(double min, double max) {
            Random random = new Random();
            double price = min + (max - min) * random.nextDouble();

            BigDecimal bd = new BigDecimal(price);
            bd = bd.setScale(2, RoundingMode.HALF_UP);

            return bd.doubleValue();
        }

        /**
         * Добавляет предмет в инвентарь продавца с заданными лимитом и ценой.
         *
         * @param inventory Инвентарь продавца.
         * @param item      Предмет, который нужно добавить.
         * @param limit     Лимит продаж предмета.
         * @param price     Цена предмета.
         */
        public static void addItemToInventory(Inventory inventory, ItemStack item, int limit, double price) {
            ItemStack itemInInventory = new ItemStack(item.getType(), 1);
            ItemMeta meta = itemInInventory.getItemMeta();
            if (meta == null) {
                return;
            }

            String displayName = ChatColor.GREEN + getLocalizedItemName(item);
            meta.setDisplayName(displayName);

            List<String> lore = new ArrayList<>();
            double lorePrice = price * 64;
            lore.add(ChatColor.GRAY + "-----------------------");
            lore.add(ChatColor.WHITE + "Цена за " + ChatColor.BOLD + "x1: " + ChatColor.GOLD + price);
            lore.add(ChatColor.WHITE + "Цена за " + ChatColor.BOLD + "x64: " + ChatColor.GOLD + lorePrice);
            lore.add("");
            lore.add(ChatColor.YELLOW + "Лимит: " + ChatColor.RED + limit);
            lore.add("");
            lore.add(ChatColor.WHITE + "Продать " + ChatColor.AQUA + "x1: " + ChatColor.GOLD + "(ПКМ)");
            lore.add(ChatColor.WHITE + "Продать " + ChatColor.AQUA + "x64: " + ChatColor.GOLD + "(ЛКМ)");
            lore.add(ChatColor.GRAY + "-----------------------");
            meta.setLore(lore);

            itemInInventory.setItemMeta(meta);

            for (ItemStack invItem : inventory.getContents()) {
                if (invItem != null && invItem.getType() == item.getType()) {
                    inventory.remove(invItem);
                    break;
                }
            }

            inventory.addItem(itemInInventory);
        }

        private static String getLocalizedItemName(ItemStack item) {
            return item.getType().getKey().getKey().replace("_", " ").toUpperCase();
        }

        private void saveItemToConfig(String name, int amount, double minPrice, double maxPrice) {
            File file = new File(plugin.getDataFolder(), "config/items.json");
            List<Item> items = new ArrayList<>();

            if (file.exists()) {
                items = loadItemsFromFile(file);
            }

            items.add(new Item(name, amount, minPrice, maxPrice));

            try (FileWriter writer = new FileWriter(file)) {
                objectMapper.writeValue(writer, items);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private List<Item> loadItemsFromFile(File file) {
            try {
                return objectMapper.readValue(file, objectMapper.getTypeFactory().constructCollectionType(List.class, Item.class));
            } catch (IOException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent event) {
            File configFile = new File(plugin.getDataFolder(), "config/config.yaml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
            if (!(event.getWhoClicked() instanceof Player)) {
                return;
            }

            Player player = (Player) event.getWhoClicked();

            if (event.getView().getTitle().equals("Скупщик")) {
                event.setCancelled(true);

                ItemStack clickedItem = event.getCurrentItem();
                if (clickedItem != null && clickedItem.getType() == Material.RED_STAINED_GLASS_PANE) {
                    player.closeInventory();
                }
            }

            if (event.getClickedInventory() == null || !event.getClickedInventory().equals(getSellerInventory())) {
                return;
            }

            if (event.getClickedInventory().getType() != InventoryType.CHEST) {
                return;
            }

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getType() == Material.AIR) {
                return;
            }

            ItemMeta meta = clickedItem.getItemMeta();
            if (meta == null) {
                return;
            }

            List<String> lore = meta.getLore();

            String limitPart = lore.get(4);
            String pricePart = lore.get(1);

            String[] limitParts = limitPart.split(": ");
            String[] priceParts = pricePart.split(": ");

            int limit;
            double price;

            try {
                limit = Integer.parseInt(limitParts[1].replaceAll("§[0-9a-fk-or]", ""));
                price = Double.parseDouble(priceParts[1].replaceAll("§[0-9a-fk-or]", ""));
            } catch (NumberFormatException e) {
                Chat.sendMessage(player, config.getString("sellErrorDetails"));
                e.printStackTrace();
                return;
            }

            int amountToSell = event.isRightClick() ? 1 : event.isLeftClick() ? 64 : 0;

            if (amountToSell > limit) {
                if (limit <= 0) {
                    Chat.sendMessage(player, config.getString("limitReached"));
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 2, 2);
                    event.setCancelled(true);
                    return;
                }
                amountToSell = limit;
            }

            ItemStack itemInInventory = new ItemStack(clickedItem.getType(), amountToSell);
            if (!player.getInventory().containsAtLeast(itemInInventory, amountToSell)) {
                Chat.sendMessage(player, config.getString("sellNoItems"));
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 2, 2);
                event.setCancelled(true);
                return;
            }

            limit -= amountToSell;
            BigDecimal bdPrice = BigDecimal.valueOf(price * amountToSell).setScale(2, RoundingMode.HALF_UP);
            EconomyResponse response = economy.depositPlayer(player, bdPrice.doubleValue());

            if (response.transactionSuccess()) {
                player.getInventory().removeItem(new ItemStack(clickedItem.getType(), amountToSell));
                Chat.sendMessage(player, config.getString("sellSuccess") + amountToSell + " " + clickedItem.getType().name() + config.getString("sellAmount") + bdPrice.doubleValue());
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_TRADE, 2, 2);

                Inventory inventory = getSellerInventory();
                inventory.removeItem(clickedItem);

                ItemStack newItem = clickedItem.clone();
                ItemMeta newMeta = newItem.getItemMeta();
                if (newMeta != null) {
                    newMeta.setDisplayName(ChatColor.GREEN + getLocalizedItemName(clickedItem) + ChatColor.WHITE);
                    List<String> newLore = new ArrayList<>();
                    double lorePrice = price * 64;
                    newLore.add(ChatColor.GRAY + "-----------------------");
                    newLore.add(ChatColor.WHITE + "Купит " + ChatColor.BOLD + "x1  " + ChatColor.GRAY + " (ПКМ)" + ChatColor.WHITE + " за " + ChatColor.GOLD + price);
                    newLore.add(ChatColor.WHITE + "Купит " + ChatColor.BOLD + "x64 " + ChatColor.GRAY + " (ЛКМ)" + ChatColor.WHITE + " за " + ChatColor.GOLD + lorePrice);
                    newLore.add("");
                    newLore.add(ChatColor.YELLOW + "Лимит: " + ChatColor.RED + limit);
                    newLore.add("");
                    newLore.add(ChatColor.WHITE + "Продать " + ChatColor.AQUA + "x1: " + ChatColor.GOLD + "(ПКМ)");
                    newLore.add(ChatColor.WHITE + "Продать " + ChatColor.AQUA + "x64: " + ChatColor.GOLD + "(ЛКМ)");
                    newLore.add(ChatColor.GRAY + "-----------------------");
                    newMeta.setLore(newLore);
                    newItem.setItemMeta(newMeta);
                }
                addItemToInventory(inventory, newItem, limit, price);
            } else {
                Chat.sendMessage(player, config.getString("sellError") + response.errorMessage);
            }

            event.setCancelled(true);
        }
    }

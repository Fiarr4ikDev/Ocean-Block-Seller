package ru.pechan.oceanblockseller.command;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static ru.pechan.oceanblockseller.OceanBlockSeller.getEconomy;
import static ru.pechan.oceanblockseller.OceanBlockSeller.getSellerInventory;

    /**
     * Обработчик команд и слушатель событий для команды продавца и взаимодействия с инвентарём.
     */
    public class SellerCommand implements CommandExecutor, Listener {

        private final Economy economy;

        /**
         * Конструктор для инициализации экземпляра экономики.
         *
         * @param plugin Экземпляр плагина, используемый для инициализации экономики.
         */
        public SellerCommand(JavaPlugin plugin) {
            this.economy = getEconomy();
        }

        /**
         * Обрабатывает команду '/seller' для открытия инвентаря продавца или добавления предметов в него.
         *
         * @param sender Отправитель команды.
         * @param command Команда, которая выполняется.
         * @param label Псевдоним команды, которая выполняется.
         * @param args Аргументы, переданные с командой.
         * @return true, если команда успешно выполнена, иначе false.
         */
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (command.getName().equalsIgnoreCase("seller")) {
                    if (args.length == 0) {
                        player.openInventory(getSellerInventory());
                        player.sendMessage("Вы открыли инвентарь продавца.");
                    } else if (args.length == 4 && args[0].equalsIgnoreCase("sell")) {
                        try {
                            int limit = Integer.parseInt(args[1]);
                            double minPrice = Double.parseDouble(args[2]);
                            double maxPrice = Double.parseDouble(args[3]);
                            ItemStack itemInHand = player.getInventory().getItemInMainHand();

                            if (itemInHand != null && itemInHand.getType() != Material.AIR) {
                                double price = getRandomPrice(minPrice, maxPrice);

                                Inventory inventory = getSellerInventory();
                                addItemToInventory(inventory, itemInHand, limit, price);
                                player.sendMessage("Предмет добавлен в инвентарь продавца с ценой: " + price + " и лимитом: " + limit);
                            } else {
                                player.sendMessage("У вас в руке нет предмета для продажи.");
                            }
                        } catch (NumberFormatException e) {
                            player.sendMessage("Параметры цены и лимита должны быть числами.");
                        }
                    } else {
                        player.sendMessage("Неправильное использование команды. Правильный формат: /seller sell <limit> <minprice> <maxprice>");
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
        private double getRandomPrice(double min, double max) {
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
         * @param item Предмет, который нужно добавить.
         * @param limit Лимит продаж предмета.
         * @param price Цена предмета.
         */
        private void addItemToInventory(Inventory inventory, ItemStack item, int limit, double price) {
            ItemStack itemInInventory = new ItemStack(item.getType(), 1);
            ItemMeta meta = itemInInventory.getItemMeta();
            if (meta == null) {
                return;
            }

            String itemName = item.getType().name();
            String displayName = ChatColor.GREEN + itemName + ChatColor.WHITE;

            meta.setDisplayName(displayName);

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "------------------");
            lore.add(ChatColor.WHITE + "Цена за " + ChatColor.BOLD + "x1: " + ChatColor.GOLD + price);
            lore.add(ChatColor.WHITE + "Цена за " + ChatColor.BOLD + "x64: " + ChatColor.GOLD + price * 64);
            lore.add("");
            lore.add(ChatColor.YELLOW + "Лимит: " + ChatColor.RED + limit);
            lore.add(ChatColor.GRAY + "------------------");

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

        /**
         * Обрабатывает клики по предметам в инвентаре продавца.
         * Уменьшает лимит предмета, если он есть в инвентаре игрока, и начисляет деньги.
         *
         * @param event Событие клика по инвентарю.
         */
        @EventHandler
        public void onInventoryClick(InventoryClickEvent event) {
            Player player = (Player) event.getWhoClicked();
            if (event.getView().getTitle().equals("Продавец")) {
                event.setCancelled(true);

                ItemStack clickedItem = event.getCurrentItem();
                if (clickedItem != null && clickedItem.getType() == Material.BARRIER) {
                    player.sendMessage("Инвентарь закрыт");
                    player.closeInventory();
                }
            }

            if (!(event.getWhoClicked() instanceof Player)) {
                return;
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

            String limitPart = lore.get(1);
            String pricePart = lore.get(2);

            String[] limitParts = limitPart.split(": ");
            String[] priceParts = pricePart.split(": ");

            if (limitParts.length < 2 || priceParts.length < 2) {
                player.sendMessage("Ошибка при получении лимита или цены предмета.");
                return;
            }

            int limit;
            double price;

            try {
                limit = Integer.parseInt(limitParts[1].replaceAll("§[0-9a-fk-or]", ""));
                price = Double.parseDouble(priceParts[1].replaceAll("§[0-9a-fk-or]", ""));
            } catch (NumberFormatException e) {
                player.sendMessage("Ошибка при получении лимита или цены предмета.");
                e.printStackTrace();
                return;
            }

            if (limit <= 0) {
                Inventory inventory = getSellerInventory();
                inventory.removeItem(clickedItem);
                player.sendMessage("Лимит продаж достиг нуля. Предмет удален из инвентаря продавца.");
                event.setCancelled(true);
                return;
            }

            ItemStack itemInInventory = new ItemStack(clickedItem.getType(), 1);
            if (!player.getInventory().containsAtLeast(itemInInventory, 1)) {
                player.sendMessage("У вас нет необходимых предметов для продажи.");
                event.setCancelled(true);
                return;
            }

            limit--;
            BigDecimal bdPrice = BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP);
            EconomyResponse response = economy.depositPlayer(player, bdPrice.doubleValue());

            if (response.transactionSuccess()) {

                player.getInventory().removeItem(new ItemStack(clickedItem.getType(), 1));
                player.sendMessage("Вы продали " + clickedItem.getType().name() + " за " + bdPrice.doubleValue() + "!");

                Inventory inventory = getSellerInventory();
                inventory.removeItem(clickedItem);

                ItemStack newItem = clickedItem.clone();
                ItemMeta newMeta = newItem.getItemMeta();
                if (newMeta != null) {
                    newMeta.setDisplayName(ChatColor.GREEN + newItem.getType().name() + ChatColor.WHITE);
                    List<String> newLore = new ArrayList<>();
                    newLore.add(ChatColor.GRAY + "------------------");
                    newLore.add(ChatColor.WHITE + "Цена за " + ChatColor.BOLD + "x1: " + ChatColor.GOLD + price);
                    newLore.add(ChatColor.WHITE + "Цена за " + ChatColor.BOLD + "x64: " + ChatColor.GOLD + price * 64);
                    newLore.add("");
                    newLore.add(ChatColor.YELLOW + "Лимит: " + ChatColor.RED + limit);
                    newLore.add(ChatColor.GRAY + "------------------");
                    newMeta.setLore(newLore);
                    newItem.setItemMeta(newMeta);
                }
                addItemToInventory(inventory, newItem, limit, price);
            } else {
                player.sendMessage("Ошибка при продаже предмета: " + response.errorMessage);
            }

            event.setCancelled(true);
        }

    }

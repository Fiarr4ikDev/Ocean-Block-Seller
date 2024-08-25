package ru.pechan.oceanblockseller;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import ru.pechan.oceanblockseller.command.SellerCommand;

    public final class OceanBlockSeller extends JavaPlugin implements Listener {

        @Override
        public void onEnable() {
            getLogger().info("OceanBlockSeller запущен");

            getCommand("seller").setExecutor(new SellerCommand());
            Bukkit.getPluginManager().registerEvents(this, this);
        }

        @Override
        public void onDisable() {

        }

        private static Inventory sharedSellerInventory;

        public static Inventory getSellerInventory() {

            if (sharedSellerInventory == null) {
                sharedSellerInventory = Bukkit.createInventory(null, 54, "Продавец");

                // Огородка красным стеклом
                ItemStack glass = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
                setItemStackName(glass, " ");
                for (int i = 36; i <= 44; i++) {
                    sharedSellerInventory.setItem(i, glass);
                }

                // Кнопка выхода
                ItemStack exit = new ItemStack(Material.BARRIER, 1);
                setItemStackName(exit, "§cЗакрыть");
                sharedSellerInventory.setItem(49, exit);
            }

            return sharedSellerInventory;
        }

        public static void setItemStackName(ItemStack renamed, String customName) {
            ItemMeta renamedMeta = renamed.getItemMeta();
            renamedMeta.setDisplayName(customName);
            renamed.setItemMeta(renamedMeta);
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent event) {
            Player player = (Player) event.getWhoClicked();
            if (event.getView().getTitle().equals("Продавец")) {
                player.sendMessage("Нельзя брать предметы");
                event.setCancelled(true);

                ItemStack clickedItem = event.getCurrentItem();
                if (clickedItem != null && clickedItem.getType() == Material.BARRIER) {
                    player.sendMessage("Инвентарь закрыт");
                    player.closeInventory();
                }
            }
        }
    }

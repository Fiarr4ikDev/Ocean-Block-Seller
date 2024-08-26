package ru.pechan.oceanblockseller;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import ru.pechan.oceanblockseller.command.BalanceCommand;
import ru.pechan.oceanblockseller.command.SellerCommand;

    public final class OceanBlockSeller extends JavaPlugin implements Listener {

        private final JavaPlugin plugin = this;
        private static Economy econ = null;
        private static Permission perms = null;
        private static Chat chat = null;

        @Override
        public void onEnable() {
            setupEconomy();
            setupSellerInventory();

            getCommand("seller").setExecutor(new SellerCommand(this));
            getCommand("balance").setExecutor(new BalanceCommand(this));
            getServer().getPluginManager().registerEvents(new SellerCommand(this), this);
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

        /**
         * Устанавливает имя для предмета.
         *
         * @param item Предмет.
         * @param name Имя, которое нужно установить.
         */
        private static void setItemStackName(ItemStack item, String name) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(name);
                item.setItemMeta(meta);
            }
        }

        public static Economy getEconomy() {
            return econ;
        }

        public static Permission getPermissions() {
            return perms;
        }

        public static Chat getChat() {
            return chat;
        }
    }

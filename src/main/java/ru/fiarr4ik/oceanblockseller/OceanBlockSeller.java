package ru.fiarr4ik.oceanblockseller;

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
import ru.fiarr4ik.oceanblockseller.command.BalanceCommand;
import ru.fiarr4ik.oceanblockseller.command.ReloadTradesCommand;
import ru.fiarr4ik.oceanblockseller.command.SellerCommand;

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
            getCommand("reloadsell").setExecutor(new ReloadTradesCommand(this));
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

                ItemStack timer = new ItemStack(Material.CLOCK, 1);
                setItemStackName(timer, "TODO таймер");

                ItemStack info = new ItemStack(Material.OAK_SIGN, 1);
                setItemStackName(info, "TODO инфо о скупщике");

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
                sharedSellerInventory.setItem(51, timer);
                sharedSellerInventory.setItem(52, blackGlass);
                sharedSellerInventory.setItem(53, blackGlass);



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

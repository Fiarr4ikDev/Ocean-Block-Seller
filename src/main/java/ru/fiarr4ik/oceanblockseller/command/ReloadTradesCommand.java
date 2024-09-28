package ru.fiarr4ik.oceanblockseller.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.fiarr4ik.oceanblockseller.utils.UtilityClass;

import java.io.File;

/**
     * Команда для обновления товаров у скупщика через JSON конфиг
     */
    public class ReloadTradesCommand implements CommandExecutor {

        private final JavaPlugin plugin;
        private final String serverPluginName = ChatColor.AQUA + "OceanSeller | ";

        public ReloadTradesCommand(JavaPlugin plugin) {
            this.plugin = plugin;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            Player player = (Player) sender;
            File file = new File(plugin.getDataFolder(), "config/items.json");

            if (player.hasPermission("itembuyer.selleredit")) {
                UtilityClass.loadTrades(player, file);
            } else {
                player.sendMessage(serverPluginName + ChatColor.RED + "У вас недостаточно прав на использование команды.");
            }

            return true;
        }

    }

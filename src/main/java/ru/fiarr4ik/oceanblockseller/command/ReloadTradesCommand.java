package ru.fiarr4ik.oceanblockseller.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.fiarr4ik.oceanblockseller.service.ConfigService;
import ru.fiarr4ik.oceanblockseller.utils.UtilityClass;

import java.io.File;
import java.util.Objects;

    /**
     * Команда для обновления товаров у скупщика через JSON конфиг
     */
    public class ReloadTradesCommand implements CommandExecutor {

        private final JavaPlugin plugin;
        private final ConfigService configService;
        private final UtilityClass utilityClass;

        public ReloadTradesCommand(JavaPlugin plugin) {
            this.plugin = plugin;
            this.configService = new ConfigService(plugin);
            this.utilityClass = new UtilityClass(plugin);
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            Player player = (Player) sender;
            File file = new File(plugin.getDataFolder(), "config/items.json");

            if (player.hasPermission("itembuyer.selleredit")) {
                utilityClass.loadTrades(player, file);
            } else {
                player.sendMessage(Objects.requireNonNull(configService.getConfig().getString("messages.noPermission")));
            }

            return true;
        }

    }

package ru.fiarr4ik.oceanblockseller.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.fiarr4ik.oceanblockseller.service.ConfigService;
import ru.fiarr4ik.oceanblockseller.service.TradeService;

import java.io.File;
import java.util.Objects;

    public class ReloadTradesCommand implements CommandExecutor {

        private final ConfigService configService;
        private final TradeService tradeService;
        private final File itemConfig;

        public ReloadTradesCommand(JavaPlugin plugin) {
            this.configService = new ConfigService(plugin);
            this.tradeService = new TradeService(plugin);
            this.itemConfig = configService.getItemConfig();
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            Player player = (Player) sender;

            if (player.hasPermission("itembuyer.selleredit")) {
                tradeService.loadTrades(player, itemConfig);
            } else {
                player.sendMessage(Objects.requireNonNull(configService.getConfig().getString("messages.noPermission")));
            }

            return true;
        }

    }

package ru.fiarr4ik.oceanblockseller.command;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static ru.fiarr4ik.oceanblockseller.OceanBlockSeller.getEconomy;

    /**
     * Команда для проверки баланса игрока с округлением до сотых.
     */
    public class BalanceCommand implements CommandExecutor {

        private Economy economy;
        private String serverPluginName = ChatColor.AQUA + "OceanSeller  ";

        /**
         * Конструктор для инициализации экземпляра экономики.
         *
         * @param plugin Экземпляр плагина.
         */
        public BalanceCommand(JavaPlugin plugin) {
            this.economy = getEconomy();
        }

        /**
         * Обрабатывает команду '/balance' для получения баланса игрока.
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

                double balance = economy.getBalance(player);

                BigDecimal roundedBalance = new BigDecimal(balance).setScale(2, RoundingMode.HALF_UP);

                player.sendMessage(serverPluginName + ChatColor.GREEN + "Ваш текущий баланс: " + ChatColor.GOLD + roundedBalance);

                return true;
            } else {
                sender.sendMessage(serverPluginName + ChatColor.RED + "У вас недостаточно прав на использование команды.");
                return false;
            }
        }
    }

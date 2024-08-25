package ru.pechan.oceanblockseller.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.pechan.oceanblockseller.OceanBlockSeller;

    public class SellerCommand implements CommandExecutor, Listener {

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (args.length == 0) {
                    player.openInventory(OceanBlockSeller.getSellerInventory());
                    return true;
                } else if (args[0].equalsIgnoreCase("sell")) {
                    ItemStack item = player.getInventory().getItemInMainHand();
                    Inventory inv = OceanBlockSeller.getSellerInventory();

                    boolean itemAdded = false;
                    for (int i = 0; i <= 35; i++) {
                        if (inv.getItem(i) == null || inv.getItem(i).getType().isAir()) {
                            inv.setItem(i, item);
                            player.getInventory().setItemInMainHand(null);
                            itemAdded = true;
                            break;
                        }
                    }
                    if (itemAdded) {
                        player.sendMessage("§aПредмет успешно продан!");
                    } else {
                        player.sendMessage("§cИнвентарь продавца заполнен.");
                    }
                    return true;
                } else {
                    return false;
                }
            }
            return false;
        }
    }
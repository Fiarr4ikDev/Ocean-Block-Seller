package ru.fiarr4ik.oceanblockseller.command;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import ru.fiarr4ik.oceanblockseller.config.Item;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static ru.fiarr4ik.oceanblockseller.OceanBlockSeller.getSellerInventory;
import static ru.fiarr4ik.oceanblockseller.command.SellerCommand.addItemToInventory;
import static ru.fiarr4ik.oceanblockseller.command.SellerCommand.getRandomPrice;

    /**
     * Команда для обновления товаров у скупщика через JSON конфиг
     */
    public class ReloadTradesCommand implements CommandExecutor {

        private final JavaPlugin plugin;
        private ObjectMapper objectMapper;
        private String serverPluginName = ChatColor.AQUA + "OceanSeller  ";

        public ReloadTradesCommand(JavaPlugin plugin) {
            this.plugin = plugin;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            Player player = (Player) sender;
            objectMapper = new ObjectMapper();

            File file = new File(plugin.getDataFolder(), "config/items.json");

            try {
                List<Item> items = objectMapper.readValue(file, new TypeReference<List<Item>>() {});
                if (items.size() > 28) {
                    player.sendMessage(serverPluginName + ChatColor.RED + " Количество предметов в конфиге больше вместительности скупщика. (28)");
                } else {
                    Inventory inventory = getSellerInventory();

                    for (Item item : items) {
                        Material material = Material.getMaterial(item.getName().toUpperCase());
                        ItemStack itemStack = new ItemStack(material);
                        addItemToInventory(inventory, itemStack, item.getAmount(), getRandomPrice(item.getMinPrice(), item.getMaxPrice()));
                    }
                    player.sendMessage(serverPluginName + "Предметы успешно выставлены на скупку");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return true;
        }

    }

package ru.fiarr4ik.oceanblockseller;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import ru.fiarr4ik.oceanblockseller.command.ReloadTradesCommand;
import ru.fiarr4ik.oceanblockseller.command.SellerCommand;
import ru.fiarr4ik.oceanblockseller.command.SellerTabCompleter;
import ru.fiarr4ik.oceanblockseller.service.ConfigService;
import ru.fiarr4ik.oceanblockseller.service.EconomyService;
import ru.fiarr4ik.oceanblockseller.service.InventoryService;
import ru.fiarr4ik.oceanblockseller.service.TimeService;
import ru.fiarr4ik.oceanblockseller.service.TradeService;

import java.io.File;

    public final class OceanBlockSeller extends JavaPlugin implements Listener {

        private static OceanBlockSeller plugin;
        private final File itemConfig;
        private final ConfigService configService;
        private final TradeService tradeService;
        private final InventoryService inventoryService;
        private final TimeService timeService;
        private EconomyService economyService;

        public OceanBlockSeller() {
            this.configService = new ConfigService(this);
            this.tradeService = new TradeService(this);
            this.inventoryService = new InventoryService(this);
            this.timeService = new TimeService(this);
            this.itemConfig = configService.getItemConfig();
        }

        @Override
        public void onEnable() {
            getLogger().info("OceanBlockSeller запущен (удивительно)");
            plugin = this;
            inventoryService.getSellerInventory();
            economyService = new EconomyService(this);

            getCommand("seller").setExecutor(new SellerCommand(this));
            getCommand("seller").setTabCompleter(new SellerTabCompleter());
            getCommand("reloadsell").setExecutor(new ReloadTradesCommand(this));
            getServer().getPluginManager().registerEvents(new SellerCommand(this), this);

            timeService.startTimer();
            Player player = Bukkit.getPlayer(getServer().getConsoleSender().getName());
            tradeService.loadTrades(player, itemConfig);
        }

    }

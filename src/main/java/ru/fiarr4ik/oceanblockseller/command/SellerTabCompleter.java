package ru.fiarr4ik.oceanblockseller.command;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

    public class SellerTabCompleter implements TabCompleter {

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
            List<String> completions = new ArrayList<>();

            if (sender instanceof Player) {

                if (args.length == 1) {
                    List<String> subCommands = Arrays.asList("sell");
                    StringUtil.copyPartialMatches(args[0], subCommands, completions);
                } else if (args.length == 5) {

                    for (Material material : Material.values()) {
                        if (material.isItem()) {
                            completions.add(material.name().toLowerCase());
                        }
                    }
                }
            }

            completions.sort(String::compareToIgnoreCase);
            return completions;
        }
    }

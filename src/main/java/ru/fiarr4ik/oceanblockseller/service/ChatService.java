package ru.fiarr4ik.oceanblockseller.service;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

    public class ChatService {

        public ChatService() {

        }

        public String color(String from) {
            Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
            for (Matcher matcher = pattern.matcher(from); matcher.find(); matcher = pattern.matcher(from)) {
                String hexCode = from.substring(matcher.start(), matcher.end());
                String replaceSharp = hexCode.replace('#', 'x');
                char[] ch = replaceSharp.toCharArray();
                StringBuilder builder = new StringBuilder();
                for (char c : ch) {
                    builder.append("&").append(c);
                }
                from = from.replace(hexCode, builder.toString());
            }

            return ChatColor.translateAlternateColorCodes('&', from);
        }

        public void sendMessage(Player p, String msg) {
            p.sendMessage(color(msg));
        }
    }

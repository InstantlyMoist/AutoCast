package me.kyllian.autocast.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class MessageUtils {

    public static String colorTranslate(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static String motdPrepare(String string) {
        return colorTranslate(string).replace("\\n", "\n");
    }

    public static String motdPrepare(OfflinePlayer player, String message) {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) message = PlaceholderAPI.setPlaceholders(player, message);
        return colorTranslate(message).replace("\\n", "\n");
    }

    public static String prepareMessage(Player player, String message) {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) message = PlaceholderAPI.setPlaceholders(player, message);
        return colorTranslate(message.replace("\\n", "\n"));
    }
}

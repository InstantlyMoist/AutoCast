package me.kyllian.autocast.commands;

import me.kyllian.autocast.AutoCastPlugin;
import me.kyllian.autocast.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class BroadcastCommand implements CommandExecutor {

    private AutoCastPlugin plugin;
    private String broadcastLayout;

    public BroadcastCommand(AutoCastPlugin plugin) {
        this.plugin = plugin;
        broadcastLayout = plugin.getFileHandler().getMessages().getString("PluginMessages.BroadcastLayout");
    }

    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (args.length > 0) {
            if (sender.hasPermission("autocast.broadcast")) {
                StringBuilder stringBuilder = new StringBuilder();
                Arrays.stream(args).forEach(argument -> {
                    stringBuilder.append(argument).append(" ");
                });
                String message = stringBuilder.toString().trim();
                if (args[0].equalsIgnoreCase("TITLE")) {
                    message = removeFirstWord(message);
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        AutoCastPlugin.title.sendTitle(player, 5, 100, 5, message, "");
                    }
                } else if (args[0].equalsIgnoreCase("ACTIONBAR")) {
                    message = removeFirstWord(message);
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        plugin.getaStatic().waitForActionBar(100);
                        AutoCastPlugin.actionBar.sendActionBar(player, message, 100);
                    }
                } else if (args[0].equalsIgnoreCase("BOSSBAR")) {
                    message = removeFirstWord(message);
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        plugin.getaStatic().waitForBossBar(100);
                        AutoCastPlugin.bossBar.sendBossBar(player, "WHITE;" + message, BarStyle.SOLID, 100);
                    }
                } else {
                    String finalMessage = broadcastLayout.replace("%message%", message);
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        player.sendMessage(MessageUtils.prepareMessage(player, MessageUtils.prepareMessage(player, finalMessage)));
                    });
                }
                if (plugin.getMessageHandler().isLog())
                    Bukkit.getLogger().info(broadcastLayout.replace("%message%", stringBuilder.toString().trim()));
                return true;
            }
            sender.sendMessage(plugin.getFileHandler().getNoPermissionsMessage());
            return true;
        }
        sender.sendMessage(plugin.getFileHandler().getInvalidArgumentMessage());
        return true;
    }

    public String removeFirstWord(String string) {
        String[] split = string.split(" ");
        return string.replace(split[0] + " ", "");
    }
}

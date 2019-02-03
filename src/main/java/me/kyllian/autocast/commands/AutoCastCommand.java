package me.kyllian.autocast.commands;

import me.kyllian.autocast.AutoCastPlugin;
import me.kyllian.autocast.utils.Message;
import me.kyllian.autocast.utils.MessageUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AutoCastCommand implements CommandExecutor {

    private AutoCastPlugin plugin;

    public AutoCastCommand(AutoCastPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        Player aa = (Player) sender;
        plugin.getTabList().sendTabList(aa, "&cHello!", "&aBye!");
        if (args.length == 1) { // reload, force, enable/disable, help
            if (args[0].equalsIgnoreCase("help")) {
                sender.sendMessage(MessageUtils.colorTranslate("&7----------------{ &8&lAutoCast &7}----------------"));
                sender.sendMessage(MessageUtils.colorTranslate("&7The list of the possible commands are:"));
                sender.sendMessage(MessageUtils.colorTranslate("&7/autocast reload"));
                sender.sendMessage(MessageUtils.colorTranslate("&7/autocast force (message)"));
                sender.sendMessage(MessageUtils.colorTranslate("&7/autocast enabled/disable"));
                sender.sendMessage(MessageUtils.colorTranslate("&7/autocast show/hide"));
                sender.sendMessage(MessageUtils.colorTranslate("&7/broadcast (message)"));
                sender.sendMessage(MessageUtils.colorTranslate("&7-----------------------------------------"));
                return true;
            }
            if (args[0].equalsIgnoreCase("update")) {
                if (sender.hasPermission("autocast.update")) {
                    sender.sendMessage(plugin.getFileHandler().getCheckingUpdateMessage());
                    plugin.getUpdateChecker().check();
                    sender.sendMessage(plugin.getUpdateChecker().getUpdateMessage());
                    return true;
                }
                sender.sendMessage(plugin.getFileHandler().getNoPermissionsMessage());
                return true;
            }
            // Reload subcommand
            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("autocast.reload")) {
                    plugin.getFileHandler().reloadAll();
                    sender.sendMessage(plugin.getFileHandler().getReloadedMessage());
                    return true;
                }
                sender.sendMessage(plugin.getFileHandler().getNoPermissionsMessage());
                return true;
            }

            // Force subcommand
            if (args[0].equalsIgnoreCase("force")) {
                if (sender.hasPermission("autocast.force")) {
                    sender.sendMessage(plugin.getFileHandler().getForceSendingMessage());
                    plugin.getMessageHandler().sendMessage(true);
                    return true;
                }
                sender.sendMessage(plugin.getFileHandler().getNoPermissionsMessage());
                return true;
            }

            // Enable subcommand
            if (args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("disable")) {
                if (sender.hasPermission("autocast.switchmode")) {
                    sender.sendMessage(plugin.getFileHandler().getSwitchedModeMessage(args[0].equalsIgnoreCase("enable") ? "enabled" : "disabled"));
                    plugin.getFileHandler().getDataConfiguration().set("enabled", args[0].equalsIgnoreCase("enable") ? true : false);
                    plugin.getFileHandler().saveData();
                    plugin.getMessageHandler().setEnabled(args[0].equalsIgnoreCase("enable") ? true : false);
                    return true;
                }
                sender.sendMessage(plugin.getFileHandler().getNoPermissionsMessage());
                return true;
            }

            if (args[0].equalsIgnoreCase("hide") || args[0].equalsIgnoreCase("show")) {
                if (sender.hasPermission("autocast.hideandshow")) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        sender.sendMessage(plugin.getFileHandler().getSwitchedModeMessage(args[0].equalsIgnoreCase("show") ? "shown" : "hidden"));
                        plugin.getFileHandler().getDataConfiguration().set(player.getUniqueId().toString(), args[0].equalsIgnoreCase("show") ? true : false);
                        plugin.getFileHandler().saveData();
                        plugin.getMessageHandler().setEnabled(args[0].equalsIgnoreCase("show") ? true : false);
                        return true;
                    }
                    sender.sendMessage(plugin.getFileHandler().getMustBeAPlayerMessage());
                    return true;
                }
                sender.sendMessage(plugin.getFileHandler().getNoPermissionsMessage());
                return true;
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("create")) {
                if (sender.hasPermission("autocast.create")) {
                    if (plugin.getFileHandler().getMessages().get("Messages." + args[1]) != null) {
                        sender.sendMessage(plugin.getFileHandler().getMessageExistsMessage());
                        return true;
                    }
                    plugin.getMessageHandler().createMessage(args[1]);
                    sender.sendMessage(plugin.getFileHandler().getMessageCreatedMessage());
                    return true;
                }
                sender.sendMessage(plugin.getFileHandler().getNoPermissionsMessage());
                return true;
            }
            if (args[0].equalsIgnoreCase("order")) {
                if (sender.hasPermission("autocast.order")) {
                    if (args[1].equalsIgnoreCase("RANDOM") || args[1].equalsIgnoreCase("ORDERED")) {
                        boolean toRandom = args[1].equalsIgnoreCase("RANDOM");
                        plugin.getFileHandler().getConfigurationConfiguration().set("RandomOrder", toRandom);
                        sender.sendMessage(plugin.getFileHandler().getChangedOrderMessage(toRandom ? "random" : "ordered"));
                        return true;
                    }
                    sender.sendMessage(plugin.getFileHandler().getInvalidArgumentMessage());
                    return true;
                }
                sender.sendMessage(plugin.getFileHandler().getNoPermissionsMessage());
                return true;
            }
            if (args[0].equalsIgnoreCase("force")) {
                if (sender.hasPermission("autocast.force")) {
                    Message chosen = null;
                    for (Message message : plugin.getMessageHandler().getMessageList()) {
                        if (message.getName().equalsIgnoreCase(args[1])) chosen = plugin.getMessageHandler().getMessageList().get(plugin.getMessageHandler().getMessageList().indexOf(message));
                    }
                    for (Message message : plugin.getMessageHandler().getOtherMessages()) {
                        if (message.getName().equalsIgnoreCase(args[1])) chosen = plugin.getMessageHandler().getOtherMessages().get(plugin.getMessageHandler().getOtherMessages().indexOf(message));
                    }
                    if (chosen == null) {
                        sender.sendMessage(plugin.getFileHandler().getUnknownMessageMessage());
                        return true;
                    }
                    sender.sendMessage(plugin.getFileHandler().getForceSendingMessage());
                    plugin.getMessageHandler().sendFinalMessage(chosen,true);
                    return true;
                }
                sender.sendMessage(plugin.getFileHandler().getNoPermissionsMessage());
                return true;
            }
        }
        sender.sendMessage(plugin.getFileHandler().getInvalidArgumentMessage());
        return true;
    }
}

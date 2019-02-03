package me.kyllian.autocast.listeners;

import me.kyllian.autocast.AutoCastPlugin;
import me.kyllian.autocast.utils.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.regex.Matcher;

public class PlayerJoinListener implements Listener {

    private AutoCastPlugin plugin;
    private Message otherJoinMessage;
    private Message firstJoinMessage;
    private boolean enabled;
    private boolean clearChat;
    private boolean disableOther;

    public PlayerJoinListener(AutoCastPlugin plugin) {
        this.plugin = plugin;
        otherJoinMessage = new Message("Messages.OtherJoinMessage", plugin.getFileHandler());
        firstJoinMessage = new Message("Messages.FirstJoinMessage", plugin.getFileHandler());
        Bukkit.getPluginManager().registerEvents(this, plugin);

        enabled = plugin.getFileHandler().getConfigurationConfiguration().getBoolean("OnJoin.SendMessage");
        clearChat = plugin.getFileHandler().getConfigurationConfiguration().getBoolean("OnJoin.ClearChat");
        disableOther = plugin.getFileHandler().getConfigurationConfiguration().getBoolean("OnJoin.DisableOthers");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getFileHandler().getDataConfiguration().set(player.getUniqueId().toString() + ".lastIP", player.getAddress().getAddress().toString().replace("/", ""));
        plugin.getFileHandler().saveData();
        if (disableOther) event.setJoinMessage(null);
        if (clearChat) for (int i = 0; i < 100; i++) player.sendMessage(" ");
        if (enabled) plugin.getMessageHandler().sendMessage(player.hasPlayedBefore() ? otherJoinMessage : firstJoinMessage, player);
        if (player.hasPermission("autocast.update") && plugin.isUpdateCheck()) player.sendMessage(plugin.getUpdateChecker().getUpdateMessage());
    }
}

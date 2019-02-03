package me.kyllian.autocast.listeners;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderHook;
import me.kyllian.autocast.AutoCastPlugin;
import me.kyllian.autocast.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class ServerPingListener implements Listener {

    private AutoCastPlugin plugin;

    private boolean MOTDEnabled;
    private List<String> playerKnown;
    private List<String> playerUnknown;
    private int customSlot;

    public ServerPingListener(AutoCastPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);

        MOTDEnabled = plugin.getFileHandler().getMessages().getBoolean("MOTD.Enabled");
        customSlot = plugin.getFileHandler().getMessages().getInt("MOTD.CustomSlot");

        playerKnown = new ArrayList<>(plugin.getFileHandler().getMessages().getStringList("MOTD.PlayerKnown"));
        playerUnknown = new ArrayList<>(plugin.getFileHandler().getMessages().getStringList("MOTD.PlayerUnknown"));
    }

    @EventHandler
    public void onServerPing(ServerListPingEvent event) {
        if (!MOTDEnabled) return;
        String adress = event.getAddress().toString().replace("/", "");
        //TODO: Check if adress in known
        event.setMaxPlayers(customSlot);
        for (String uuid : plugin.getFileHandler().getDataConfiguration().getConfigurationSection("").getKeys(false)) {
            String lastIP = plugin.getFileHandler().getDataConfiguration().getString(uuid + ".lastIP");
            if (lastIP != null && lastIP.equalsIgnoreCase(adress)) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                event.setMotd(MessageUtils.motdPrepare(player, playerKnown.get(ThreadLocalRandom.current().nextInt(playerKnown.size()))));
                return;
            }
        }
        event.setMotd(MessageUtils.motdPrepare(playerUnknown.get(ThreadLocalRandom.current().nextInt(playerUnknown.size()))));
    }
}

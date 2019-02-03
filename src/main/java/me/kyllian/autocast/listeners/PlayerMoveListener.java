package me.kyllian.autocast.listeners;

import me.kyllian.autocast.AutoCastPlugin;
import me.kyllian.autocast.utils.Message;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class PlayerMoveListener implements Listener {

    private AutoCastPlugin plugin;
    private boolean isEnabled;
    private List<String> addedRegions;
    private HashMap<String, Message> regionMessages;

    public PlayerMoveListener(AutoCastPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        isEnabled = Bukkit.getPluginManager().isPluginEnabled("WorldGuard");
        addedRegions = new ArrayList<>();
        regionMessages = new HashMap<>();
        for (String entry : plugin.getFileHandler().getConfigurationConfiguration().getStringList("WorldGuard")) {
            String[] split = entry.split(";");
            addedRegions.add(split[0]);
            regionMessages.put(split[0], new Message("Messages." + split[1], plugin.getFileHandler()));
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Bukkit.broadcastMessage("test");
        if (!isEnabled) return;
        Bukkit.broadcastMessage("run");
        Player player = event.getPlayer();
        if (event.getTo().getBlock().equals(event.getFrom().getBlock())) return;
        new BukkitRunnable() {
            public void run() {
                Bukkit.broadcastMessage("test");
                /*getJoinedRegions(event.getFrom(), event.getTo()).forEach(region -> {
                    Bukkit.broadcastMessage("Joined a region");
                    regionMessages.get(region).send(player);
                });*/
            }
        }.runTaskAsynchronously(plugin);
    }

    /*public Set<ProtectedRegion> getJoinedRegions(Location from, Location to) {
        Set<ProtectedRegion> oldRegions = WGBukkit.getRegionManager(from.getWorld()).getApplicableRegions(from).getRegions();
        Set<ProtectedRegion> newRegions = WGBukkit.getRegionManager(to.getWorld()).getApplicableRegions(to).getRegions();
        newRegions.removeAll(oldRegions);
        return newRegions;
    }*/

}

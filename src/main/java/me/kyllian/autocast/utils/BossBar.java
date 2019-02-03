package me.kyllian.autocast.utils;

import me.kyllian.autocast.AutoCastPlugin;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class BossBar {

    private AutoCastPlugin plugin;

    public BossBar(AutoCastPlugin plugin) {
        this.plugin = plugin;
    }

    public void sendBossBar(Player player, String message, BarStyle style, BarColor color, int duration, double progress) {
        message = MessageUtils.prepareMessage(player, message);
        org.bukkit.boss.BossBar bossBar = Bukkit.createBossBar(message, color, style);
        bossBar.setProgress(progress);
        bossBar.addPlayer(player);
        new BukkitRunnable() {
            public void run() {
                bossBar.removeAll();
                cancel();
                return;
            }
        }.runTaskLaterAsynchronously(plugin, duration);
    }

    public void sendBossBar(Player player, String message, BarStyle style, int duration) {
        String[] split = message.split(";");
        sendBossBar(player, split[1], style, BarColor.valueOf(split[0]), duration, 1.0D);
    }

    public void sendBossBar(Player player, List<String> messages, BarStyle style, int duration) {
        plugin.getaStatic().waitForBossBar(duration);
        int durationSingle = duration / messages.size();
        new BukkitRunnable() {
            int indexNumber = 0;

            public void run() {
                if (indexNumber == messages.size()) {
                    cancel();
                    return;
                }
                String[] split = messages.get(indexNumber++).split(";");
                double progress = (double) indexNumber / (double) messages.size();
                sendBossBar(player, split[1], style, BarColor.valueOf(split[0]), durationSingle,1D - progress);
            }
        }.runTaskTimerAsynchronously(plugin, 0, durationSingle);
    }
}

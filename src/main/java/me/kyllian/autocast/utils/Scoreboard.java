package me.kyllian.autocast.utils;

import com.google.common.collect.Lists;
import me.kyllian.autocast.AutoCastPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import java.util.List;

public class Scoreboard {

    private AutoCastPlugin plugin;

    public Scoreboard(AutoCastPlugin plugin) {
        this.plugin = plugin;
    }

    public void sendScoreboard(Player player, List<String> text, String header) {
        new BukkitRunnable() {
            public void run() {
                org.bukkit.scoreboard.Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
                Objective objective = board.registerNewObjective("AutoCast", "dummy");
                objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                objective.setDisplayName(MessageUtils.prepareMessage(player, header));
                text.forEach(string -> {
                    Score score = objective.getScore(MessageUtils.prepareMessage(player, string));
                    score.setScore(text.indexOf(string));
                });
                player.setScoreboard(board);
            }
        }.runTask(plugin);
    }
}

package me.kyllian.autocast.utils;

import com.google.common.collect.Lists;
import me.kyllian.autocast.AutoCastPlugin;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Static extends BukkitRunnable {

    private AutoCastPlugin plugin;
    private int updateTime;
    private boolean actionEnabled;
    private List<String> actionText;
    private int lastActionIndex = 0;

    private boolean barEnabled;
    private List<String> barText;
    private int lastBarIndex = 0;
    private BarStyle barStyle;

    private boolean scoreboardEnabled;
    private List<List<String>> scoreboardText;
    private List<String> scoreboardHeader;
    private int lastBoardIndex = 0;

    private boolean tabEnabled;
    private List<String> tabHeader;
    private List<String> tabFooter;
    private int lastTabIndex = 0;


    public Static(AutoCastPlugin plugin) {
        this.plugin = plugin;
        scoreboardText = new LinkedList<>();
        scoreboardHeader = new ArrayList<>();
        updateTime = plugin.getFileHandler().getMessages().getInt("Static.UpdateTime");
        actionEnabled = plugin.getFileHandler().getMessages().getBoolean("Static.ActionBar.Enabled");
        barEnabled = plugin.getFileHandler().getMessages().getBoolean("Static.BossBar.Enabled");
        tabEnabled = plugin.getFileHandler().getMessages().getBoolean("Static.TabList.Enabled");
        scoreboardEnabled = plugin.getFileHandler().getMessages().getBoolean("Static.Scoreboard.Enabled");
        actionText = new ArrayList<>(plugin.getFileHandler().getMessages().getStringList("Static.ActionBar.Text"));
        barText = new ArrayList<>(plugin.getFileHandler().getMessages().getStringList("Static.BossBar.Text"));

        tabHeader = new ArrayList<>();
        tabFooter = new ArrayList<>();

        for (String entry : plugin.getFileHandler().getMessages().getConfigurationSection("Static.Scoreboard.Animation").getKeys( false)) {
            scoreboardHeader.add(plugin.getFileHandler().getMessages().getString("Static.Scoreboard.Animation." + entry + ".Header"));
            scoreboardText.add(plugin.getFileHandler().getMessages().getStringList("Static.Scoreboard.Animation." + entry + ".Text"));
        }

        for (String entry : plugin.getFileHandler().getMessages().getConfigurationSection("Static.TabList.Animation").getKeys(false)) {
            tabHeader.add(String.join("\n", plugin.getFileHandler().getMessages().getStringList("Static.TabList.Animation." + entry + ".Header")));
            tabFooter.add(String.join("\n", plugin.getFileHandler().getMessages().getStringList("Static.TabList.Animation." + entry + ".Footer")));
        }
        if (!Bukkit.getVersion().contains("1.7") && !Bukkit.getVersion().contains("1.8")) barStyle = BarStyle.valueOf(plugin.getFileHandler().getMessages().getString("Static.BossBar.BarStyle"));
        if (Bukkit.getVersion().contains("1.7") || Bukkit.getVersion().contains("1.8")) barEnabled = false;
        runTaskTimerAsynchronously(plugin, updateTime, updateTime);
    }

    public void run() {
        if (actionEnabled && plugin.getMessageHandler().actionDone) {
            String chosen;
            if (lastActionIndex == actionText.size() -1) {
                lastActionIndex = 0;
                chosen = actionText.get(0);
            } else {
                chosen = actionText.get(++lastActionIndex);
            }
            for (Player player : Bukkit.getOnlinePlayers()) {
                plugin.actionBar.sendActionBar(player, chosen, updateTime);
            }
        }
        if (barEnabled && plugin.getMessageHandler().barDone) {
            String chosen;
            if (lastBarIndex == barText.size() -1) {
                lastBarIndex = 0;
                chosen = barText.get(0);
            } else {
                chosen = barText.get(++lastBarIndex);
            }
            for (Player player : Bukkit.getOnlinePlayers()) {
                plugin.bossBar.sendBossBar(player, chosen, barStyle, updateTime);
            }
        }
        if (scoreboardEnabled) {
            String chosenHeader;
            List<String> chosenText;
            if (lastBoardIndex == scoreboardText.size() - 1) {
                lastBoardIndex = 0;
                chosenHeader = scoreboardHeader.get(0);
                chosenText = scoreboardText.get(0);
            } else {
                ++lastBoardIndex;
                chosenHeader = scoreboardHeader.get(lastBoardIndex);
                chosenText = scoreboardText.get(lastBoardIndex);
            }
            Bukkit.getOnlinePlayers().forEach(player -> {
                plugin.getScoreboard().sendScoreboard(player, Lists.reverse(chosenText), chosenHeader);
            });
        }
        if (tabEnabled) {
            String chosenHeader;
            String chosenFooter;
            if (lastTabIndex == tabHeader.size() - 1) {
                lastTabIndex = 0;
                chosenHeader = tabHeader.get(0);
                chosenFooter = tabFooter.get(0);
            } else {
                ++lastTabIndex;
                chosenHeader = tabHeader.get(lastTabIndex);
                chosenFooter = tabFooter.get(lastTabIndex);
            }
            Bukkit.getOnlinePlayers().forEach(player -> {
                plugin.getTabList().sendTabList(player, chosenHeader, chosenFooter);
            });
        }
    }

    public void waitForActionBar(int duration) {
        plugin.getMessageHandler().actionDone = false;
        new BukkitRunnable() {
            public void run() {
                plugin.getMessageHandler().actionDone = true;
            }
        }.runTaskLaterAsynchronously(plugin, duration);
    }

    public void waitForBossBar(int duration) {
        plugin.getMessageHandler().barDone = false;
        new BukkitRunnable() {
            public void run() {
                plugin.getMessageHandler().barDone = true;
            }
        }.runTaskLaterAsynchronously(plugin, duration);
    }
}

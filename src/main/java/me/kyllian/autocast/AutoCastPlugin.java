package me.kyllian.autocast;

import me.kyllian.autocast.commands.AutoCastCommand;
import me.kyllian.autocast.commands.BroadcastCommand;
import me.kyllian.autocast.listeners.PlayerJoinListener;
import me.kyllian.autocast.listeners.PlayerMoveListener;
import me.kyllian.autocast.listeners.ServerPingListener;
import me.kyllian.autocast.utils.*;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.concurrent.Callable;

public class AutoCastPlugin extends JavaPlugin {

    private FileHandler fileHandler;
    private MessageHandler messageHandler;
    public static ActionBar actionBar;
    public static Title title;
    public static BossBar bossBar;
    public static Book book;
    private Static aStatic;
    public boolean isMySQL;
    private MySQLHandler mySQLHandler;
    private UpdateChecker updateChecker;
    private Scoreboard scoreboard;
    private boolean updateCheck;
    private TabList tabList;

    //TODO: Worldguard API support

    @Override
    public void onEnable() {
        fileHandler = new FileHandler(this);
        isMySQL = fileHandler.getConfigurationConfiguration().getBoolean("MySQL");
        if (isMySQL) mySQLHandler = new MySQLHandler(this);
        updateChecker = new UpdateChecker(this, 43894);
        updateCheck = fileHandler.getConfigurationConfiguration().getBoolean("UpdateChecking");
        messageHandler = new MessageHandler(this);
        if (!Bukkit.getVersion().contains("1.7")) actionBar = new ActionBar(this);
        if (!Bukkit.getVersion().contains("1.7")) title = new Title(this);
        bossBar = new BossBar(this);
        if (!Bukkit.getVersion().contains("1.7") && !Bukkit.getVersion().contains("1.8")) book = new Book(this);
        if (!Bukkit.getVersion().contains("1.7")) scoreboard = new Scoreboard(this);
        tabList = new TabList(this);

        aStatic = new Static(this);

        new PlayerJoinListener(this);
        new ServerPingListener(this);
        //new PlayerMoveListener(this);

        Metrics metrics = new Metrics(this);
        metrics.addCustomChart(new Metrics.SingleLineChart("message_amount", new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return fileHandler.getMessages().getConfigurationSection("Messages").getKeys(false).size();
            }
        }));

        metrics.addCustomChart(new Metrics.SimplePie("mysql", new Callable<String>() {
            @Override
            public String call() throws Exception {
                return isMySQL ? "Using MySQL" : "Not using MySQL";
            }
        }));

        metrics.addCustomChart(new Metrics.SimplePie("message_delay", new Callable<String>() {
            @Override
            public String call() throws Exception {
                return getMessageHandler().getInterval() + "";
            }
        }));

        getCommand("autocast").setExecutor(new AutoCastCommand(this));
        getCommand("broadcast").setExecutor(new BroadcastCommand(this));
    }

    public FileHandler getFileHandler() {
        return fileHandler;
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public void reloadMessageHandler() {
        messageHandler = null;
        messageHandler = new MessageHandler(this);
    }

    public static void sendCommands(Player player, List<String> commands) {
        try {
            new BukkitRunnable() {
                public void run() {
                    for (String command : commands) {
                        String[] split = command.split(":");
                        String finalCommand = split[1].replace("%player%", player.getName());
                        if (split[0].equalsIgnoreCase("p")) player.performCommand(finalCommand);
                        else if (split[0].equalsIgnoreCase("c"))
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
                    }
                }
            }.runTask(AutoCastPlugin.getPlugin(AutoCastPlugin.class));
        } catch (Exception exc) { }
    }

    public  boolean hasMessagesEnabled(Player player) {
        return fileHandler.getDataConfiguration().getBoolean(player.getUniqueId().toString());
    }

    public Static getaStatic() {
        return aStatic;
    }

    public MySQLHandler getMySQLHandler() {
        return mySQLHandler;
    }

    public UpdateChecker getUpdateChecker() {
        return updateChecker;
    }

    public boolean isUpdateCheck() {
        return updateCheck;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public TabList getTabList() {
        return tabList;
    }
}

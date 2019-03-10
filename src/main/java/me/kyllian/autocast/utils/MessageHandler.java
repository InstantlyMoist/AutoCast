package me.kyllian.autocast.utils;

import me.kyllian.autocast.AutoCastPlugin;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MessageHandler extends BukkitRunnable {

    private AutoCastPlugin plugin;
    private int interval;
    private int minimumPlayers;
    private boolean enabled;
    private boolean randomOrder;
    private String lastMessage;
    private List<Message> messageList;
    private List<Message> otherMessages;
    private List<String> enabledMessages;
    private boolean log;
    public boolean actionDone = true;
    public boolean barDone = true;

    public MessageHandler(AutoCastPlugin plugin) {
        this.plugin = plugin;
        initializeHandler();
    }

    public void initializeHandler() {
        interval = plugin.getFileHandler().getConfigurationConfiguration().getInt("BroadcastInterval");
        minimumPlayers = plugin.getFileHandler().getConfigurationConfiguration().getInt("MinimumPlayers");
        messageList = new ArrayList<>();
        enabledMessages = plugin.getFileHandler().getConfigurationConfiguration().getStringList("EnabledMessages");
        randomOrder = plugin.getFileHandler().getConfigurationConfiguration().getBoolean("RandomOrder");
        otherMessages = new ArrayList<>();
        log = plugin.getFileHandler().getConfigurationConfiguration().getBoolean("log");
        if (randomOrder) lastMessage = plugin.getFileHandler().getDataConfiguration().getString("LastMessage");
        //TODO: Add mySQL support
        if (plugin.isMySQL) {
            try {
                if (plugin.getMySQLHandler().connection.isClosed()) {
                    Bukkit.getLogger().warning("MYSQL Connection closed! Not able to load messages!");
                    return;
                }
                PreparedStatement getMessages = plugin.getMySQLHandler().connection.prepareStatement("SELECT MessageName FROM " + plugin.getMySQLHandler().table);
                ResultSet set = getMessages.executeQuery();
                while (set.next()) {
                    messageList.add(new Message(plugin.getMySQLHandler().connection, plugin.getMySQLHandler().table, set.getString("MessageName")));
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        } else {
            for (String message : plugin.getFileHandler().getMessages().getConfigurationSection("Messages").getKeys(false)) {
                if (enabledMessages.contains(message))
                    messageList.add(new Message("Messages." + message, plugin.getFileHandler()));
                else otherMessages.add(new Message("Messages." + message, plugin.getFileHandler()));
            }
        }
        runTaskTimerAsynchronously(plugin, this.interval, this.interval);
    }

    public void run() {
        // TODO: Add enabled check
        if (Bukkit.getOnlinePlayers().size() >= minimumPlayers && plugin.getFileHandler().isAutocastEnabled()) sendMessage(false);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }


    public void clearMessages() {
        messageList.clear();
    }

    public void addMessage(String message) {
        messageList.add(new Message(message, plugin.getFileHandler()));
    }

    public void sendMessage(boolean override) {
        // TODO: Make sure it does them on order, or random.

        if (!plugin.getFileHandler().isAutocastEnabled() && !override) return;
        Message chosenMessage = null;
        if (randomOrder) chosenMessage = messageList.get(ThreadLocalRandom.current().nextInt(0, messageList.size()));
        else {
            if (lastMessage == null) lastMessage = messageList.get(0).getName();
            int count = -1;
            for (Message message : messageList) {
                count++;
                if (message.getName().equalsIgnoreCase(lastMessage)) {
                    if (count + 1 < messageList.size()) {
                        chosenMessage = messageList.get(++count);
                    }
                }
            }
        }
        if (chosenMessage == null) chosenMessage = messageList.get(0);
        plugin.getFileHandler().getDataConfiguration().set("LastMessage", chosenMessage.getName());
        lastMessage = chosenMessage.getName();
        plugin.getFileHandler().saveData();
        sendFinalMessage(chosenMessage, override);
    }

    public void sendFinalMessage(final Message message, boolean override) {
        if (log) message.getText().forEach(string -> {
            Bukkit.getLogger().info(MessageUtils.colorTranslate(string));
        });
        new BukkitRunnable() {
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!plugin.hasMessagesEnabled(player) && !override) return;
                    message.send(player);
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public void sendMessage(Message message, Player player) {
        message.send(player);
    }

    public List<Message> getOtherMessages() {
        return otherMessages;
    }

    public List<Message> getMessageList() {
        return messageList;
    }

    public boolean isLog() {
        return log;
    }

    public int getInterval() {
        return interval;
    }

    public void createMessage(String messageName) {
        plugin.getFileHandler().getMessages().set("Messages." + messageName + ".Text", Arrays.asList());
        plugin.getFileHandler().getMessages().set("Messages." + messageName + ".ClickAction", "OPEN_URL");
        plugin.getFileHandler().getMessages().set("Messages." + messageName + ".Click", " ");
        plugin.getFileHandler().getMessages().set("Messages." + messageName + ".HoverText", " ");
        plugin.getFileHandler().getMessages().set("Messages." + messageName + ".Sound", "NONE");
        plugin.getFileHandler().getMessages().set("Messages." + messageName + ".Permission", "NONE");
        plugin.getFileHandler().getMessages().set("Messages." + messageName + ".Commands", Arrays.asList());
        plugin.getFileHandler().getMessages().set("Messages." + messageName + ".Title", Arrays.asList());
        plugin.getFileHandler().getMessages().set("Messages." + messageName + ".TitleDuration", 10);
        plugin.getFileHandler().getMessages().set("Messages." + messageName + ".ActionBar", Arrays.asList());
        plugin.getFileHandler().getMessages().set("Messages." + messageName + ".ActionDuration", 10);
        plugin.getFileHandler().getMessages().set("Messages." + messageName + ".BossBar", Arrays.asList());
        plugin.getFileHandler().getMessages().set("Messages." + messageName + ".BossDuration", 10);
        plugin.getFileHandler().getMessages().set("Messages." + messageName + ".BossStyle", "SEGMENTED_6");
        plugin.getFileHandler().getMessages().set("Messages." + messageName + ".BookPages", Arrays.asList());
        plugin.getFileHandler().saveMessages();
    }
}

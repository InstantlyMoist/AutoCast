package me.kyllian.autocast.utils;

import me.kyllian.autocast.AutoCastPlugin;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Message {

    // Core data
    private String identifier;
    private FileHandler fileHandler;

    // Internal data storage
    private boolean hasText;
    private boolean hasClickAction;
    private boolean hasHoverText;
    private boolean hasSound;
    private boolean hasActionBar;
    private boolean hasCommands;
    private boolean hasTitle;
    private boolean hasBossBar;
    private boolean hasBook;
    private ClickEvent clickEvent;

    // Data from the message
    private List<String> chatMessage;
    private ClickEvent.Action clickAction;
    private String click;
    private String hoverText;
    private Sound sound;
    private String possibleSound;
    private String permission;
    private List<String> commands;
    private List<String> title;
    private int titleDuration;
    private List<String> actionBar;
    private int actionDuration;
    private List<String> bossBar;
    private int bossDuration;
    private String possibleBarStyle;
    private BarStyle barStyle;
    private List<String> bookPages;

    //TODO Create constructor with mysql connection

    public List<String> getFromSQLString(String string) {
        if (string == null || string.equalsIgnoreCase("")) return new ArrayList<>();
        return Arrays.asList(string.split("///"));
    }

    public Message(Connection connection, String tableName, String messageName) {
        try {
            PreparedStatement getMessage = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE MessageName = '" + messageName + "'");

            ResultSet result = getMessage.executeQuery();
            if (result.next()) {
                chatMessage = getFromSQLString(result.getString("MessageText"));
                clickAction = ClickEvent.Action.valueOf(result.getString("ClickAction"));
                click = result.getString("Click");
                hoverText = result.getString("HoverText");
                possibleSound = result.getString("Sound");
                permission = result.getString("Permission");
                commands = getFromSQLString(result.getString("Sound"));
                title = getFromSQLString(result.getString("Title"));
                titleDuration = result.getInt("TitleDuration");
                actionBar = getFromSQLString(result.getString("ActionBar"));
                actionDuration = result.getInt("ActionDuration");
                bossBar = getFromSQLString(result.getString("BossBar"));
                bossDuration = result.getInt("BossDuration");
                possibleBarStyle = result.getString("BossStyle");
                bookPages = new ArrayList<>();
                getFromSQLString(result.getString("BookPages")).forEach(page -> {
                    bookPages.add(page);
                });
            }
            identifier = "Messages.Messages." + messageName;
            result.close();
            getMessage.close();
            finalizeCoreData();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public Message(String identifier, FileHandler fileHandler) {
        this.identifier =  "Messages." + identifier;
        this.fileHandler = fileHandler;
        chatMessage = fileHandler.getMessages().getStringList(identifier + ".Text");
        clickAction = ClickEvent.Action.valueOf(fileHandler.getMessages().getString(identifier + ".ClickAction"));
        click = fileHandler.getMessages().getString(identifier + ".Click");
        hoverText = fileHandler.getMessages().getString(identifier + ".HoverText");
        possibleSound = fileHandler.getMessages().getString(identifier + ".Sound");
        permission = fileHandler.getMessages().getString(identifier + ".Permission");
        commands = fileHandler.getMessages().getStringList(identifier + ".Permission");
        title = fileHandler.getMessages().getStringList(identifier + ".Title");
        titleDuration = fileHandler.getMessages().getInt(identifier + ".TitleDuration");
        actionBar = fileHandler.getMessages().getStringList(identifier + ".ActionBar");
        actionDuration = fileHandler.getMessages().getInt(identifier + ".ActionDuration");
        bossBar = fileHandler.getMessages().getStringList(identifier + ".BossBar");
        possibleBarStyle = fileHandler.getMessages().getString(identifier + ".BossStyle");
        bossDuration = fileHandler.getMessages().getInt(identifier + ".BossDuration");
        bookPages = new ArrayList<>();
        for (String page : fileHandler.getMessages().getStringList(identifier + ".BookPages")) {
            bookPages.add(page);
        }
        finalizeCoreData();
    }

    public void finalizeCoreData() {
        hasText = !chatMessage.isEmpty();
        hasClickAction = clickAction != null;
        if (hasClickAction) clickEvent = new ClickEvent(clickAction, click);
        hasHoverText = !hoverText.equals("NONE");
        hasSound = !possibleSound.equalsIgnoreCase("NONE");
        if (possibleSound.equalsIgnoreCase("RANDOM")) sound = Arrays.asList(Sound.values()).get(ThreadLocalRandom.current().nextInt(Arrays.asList(Sound.values()).size()));
        if (hasSound && !possibleSound.equalsIgnoreCase("RANDOM")) sound = Sound.valueOf(possibleSound);
        hasCommands = !commands.isEmpty() || commands.toString().equalsIgnoreCase("[NONE]");
        hasTitle = !title.isEmpty();
        if (Bukkit.getVersion().contains("1.7")) hasTitle = false;
        hasActionBar = !actionBar.isEmpty();
        if (Bukkit.getVersion().contains("1.7")) hasActionBar = false;
        if (!Bukkit.getVersion().contains("1.7") && !Bukkit.getVersion().contains("1.8")) barStyle = BarStyle.valueOf(possibleBarStyle);
        hasBossBar = !bossBar.isEmpty();
        if (Bukkit.getVersion().contains("1.7") || Bukkit.getVersion().contains("1.8")) hasBossBar = false;
        hasBook = !bookPages.isEmpty();
        if (Bukkit.getVersion().contains("1.7") || Bukkit.getVersion().contains("1.8")) hasBook = false;
    }

    public void send(Player player) {
        if (permission != "NONE" && !player.hasPermission(permission)) return;
        if (hasText) chatMessage.forEach(message -> {
            TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText(MessageUtils.prepareMessage(player, message)));
            if (hasHoverText) textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(MessageUtils.prepareMessage(player, hoverText)).create()));
            if (hasClickAction) textComponent.setClickEvent(clickEvent);
            player.spigot().sendMessage(textComponent);
        });
        if (hasSound) player.playSound(player.getLocation(), sound, 1f, 1f);
        if (hasActionBar) AutoCastPlugin.actionBar.sendActionBar(player, actionBar, actionDuration);
        if (hasTitle) AutoCastPlugin.title.sendTitle(player, 0, 0 , titleDuration, title, titleDuration);
        if (hasBossBar) AutoCastPlugin.bossBar.sendBossBar(player, bossBar, barStyle, bossDuration);
        if (hasCommands) AutoCastPlugin.sendCommands(player, commands);
        if (hasBook) sendBook(player);
    }

    public String getName() {
        return identifier.split("\\.")[2];
    }

    public void sendBook(Player player) {
        if (Bukkit.getVersion().contains("1.7") || Bukkit.getVersion().contains("1.8")) return;
        ItemStack writtenBook = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta writtenMeta = (BookMeta) writtenBook.getItemMeta();
        List<Object> p;
        Object page;
        try {
            p = (List<Object>) ReflectionUtils.getField(ReflectionUtils.PackageType.CRAFTBUKKIT_INVENTORY.getClass("CraftMetaBook"), true, "pages").get(writtenMeta);
            for (String text : bookPages) {
                page = ReflectionUtils.invokeMethod(ReflectionUtils.PackageType.MINECRAFT_SERVER.getClass("IChatBaseComponent$ChatSerializer").newInstance(), "a", MessageUtils.prepareMessage(player, text));
                p.add(page);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        writtenBook.setItemMeta(writtenMeta);
        AutoCastPlugin.book.openBook(writtenBook, player);
    }

    public List<String> getText() {
        return chatMessage;
    }
}

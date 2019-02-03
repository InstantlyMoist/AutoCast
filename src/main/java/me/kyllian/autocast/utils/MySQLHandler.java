package me.kyllian.autocast.utils;

import me.kyllian.autocast.AutoCastPlugin;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.sql.*;
import java.util.Arrays;
import java.util.List;

public class MySQLHandler {

    private AutoCastPlugin plugin;

    // Database file data
    private FileConfiguration databaseConfiguration;
    private File databaseFile;

    // MySQL logic
    public Connection connection;
    private String host;
    private String database;
    private String username;
    private String password;
    public String table;
    private int port;

    public MySQLHandler(AutoCastPlugin plugin) {
        this.plugin = plugin;

        databaseFile = new File(plugin.getDataFolder(), "database.yml");
        if (!databaseFile.exists()) plugin.saveResource("database.yml", false);
        databaseConfiguration = YamlConfiguration.loadConfiguration(databaseFile);

        host = databaseConfiguration.getString("host");
        database = databaseConfiguration.getString("database");
        username = databaseConfiguration.getString("username");
        password = databaseConfiguration.getString("password");
        table = databaseConfiguration.getString("table");
        port = databaseConfiguration.getInt("port");

        try{
            synchronized (this){
                if(connection != null && !connection.isClosed()){
                    return;
                }

                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":"
                        + this.port + "/" + this.database, this.username, this.password);

                Bukkit.getLogger().info("MySQL Database connected!");

                PreparedStatement createDatabase = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + this.table
                + " (id INT AUTO_INCREMENT NOT NULL,"
                + "  MessageName TEXT,"
                + "  MessageText TEXT,"
                + "  ClickAction TEXT,"
                + "  Click TEXT,"
                + "  HoverText TEXT,"
                + "  Sound TEXT,"
                + "  Permission TEXT,"
                + "  Commands TEXT,"
                + "  Title TEXT,"
                + "  TitleDuration INTEGER,"
                + "  ActionBar TEXT,"
                + "  ActionDuration INTEGER,"
                + "  BossBar TEXT,"
                + "  BossStyle TEXT,"
                + "  BossDuration INTEGER,"
                + "  BookPages TEXT,"
                + "  primary key (id))");
                createDatabase.executeUpdate();
                createDatabase.close();

                PreparedStatement createMessage = connection.prepareStatement("SELECT * FROM " + table);
                ResultSet createMessageResult = createMessage.executeQuery();
                if (createMessageResult.next()) {
                    Bukkit.getLogger().info("FOUND MESSAGES");
                } else {
                    //TODO Add messages
                    String messageText = toString(Arrays.asList("&7----------------{ &8&lAutoCast &7}----------------", "&7Welcome to the server %player_name%", "&7Enjoy your stay!", "&7Click on this message to join our discord!", "&7-----------------------------------------"));
                    String title = toString(Arrays.asList("&cTitle only; ", " ;&aSubtitle only"));
                    String bossBar = toString(Arrays.asList("RED;&cBossbar one", "GREEN;&cBossbar two"));
                    String bookPages = toString(Arrays.asList("[\"\",{\"text\":\"ServerName:\",\"color\":\"gray\",\"bold\":true},{\"text\":\"\\nAutoCast welcomes you back to the server %player_name%!\",\"color\":\"gray\",\"bold\":false},{\"text\":\"\\nPlease read the whole book to gather information about the server!\",\"color\":\"gray\"}]"
                    , "[\"\",{\"text\":\"Rules:\",\"color\":\"gray\"},{\"text\":\" Hover over them to see the consequences!\",\"color\":\"gray\",\"italic\":true},{\"text\":\"\\n1. Do not swear!\",\"color\":\"gray\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"If you do so, you will receive a 24 hour mute!\",\"color\":\"red\"}]}},\"italic\":false},{\"text\":\"\\n2. Do not grief\",\"color\":\"gray\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"If you do so, you will receive a pernament ban!\",\"color\":\"red\"}]}}},{\"text\":\"\\n3. Do not spam\",\"color\":\"gray\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"If you do so, we will mute you for 2 hours\",\"color\":\"red\"}]}}}]"
                    , "[\"\",{\"text\":\"Info: \",\"color\":\"gray\"},{\"text\":\"\\nWe just updated to 1.13\",\"color\":\"gray\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"This means we now have new blocks!\",\"color\":\"gray\"}]}}},{\"text\":\"\\nWe recently installed AutoCast for a better server experience!\",\"color\":\"gray\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://www.spigotmc.org/resources/autocast.43894/\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"You can also click on here to be redirected to their download page!\",\"color\":\"gray\"}]}}},{\"text\":\"\\nWe just created a new discord server!\",\"color\":\"gray\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://discord.gg/zgKr2YM\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"You can join it by clicking on this message!\",\"color\":\"gray\"}]}}}]"));
                    PreparedStatement addMessage = connection.prepareStatement("INSERT INTO " + table
                            + " (MessageName, MessageText, ClickAction, Click, HoverText, Sound, Permission, Commands, Title, TitleDuration, ActionBar, ActionDuration, BossBar, BossStyle, BossDuration, BookPages)"
                            + " VALUES ('CreatorMessage', '" + messageText + "', 'OPEN_URL', 'https://discord.gg/zgKr2YM', '&7Click here to join the discord', 'NONE', 'NONE', '', '" + title + "', '10', '[]', '10', '" + bossBar + "', 'SEGMENTED_6', '10', '" + bookPages + "')");

                    //TODO: Create our own method to do a toString, to make sure the [] dont replaced in the book.
                    addMessage.execute();
                    addMessage.close();
                }
                createMessage.close();
            }
        }catch(SQLException e){
            e.printStackTrace();
        }catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String toString(List<String> list) {
        return StringUtils.join(list, "///");
    }
}

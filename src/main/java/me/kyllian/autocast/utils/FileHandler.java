package me.kyllian.autocast.utils;

import me.kyllian.autocast.AutoCastPlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class FileHandler {

    // Core data
    private AutoCastPlugin plugin;

    // Messages file data
    private FileConfiguration messagesConfiguration;
    private File messagesFile;

    // Configuration file data
    private FileConfiguration configurationConfiguration;
    private File configurationFile;

    // Data file data
    private FileConfiguration dataConfiguration;
    private File dataFile;

    public FileHandler(AutoCastPlugin plugin) {
        this.plugin = plugin;
        if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdir();

        // Messages file
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) plugin.saveResource("messages.yml", false);
        messagesConfiguration = YamlConfiguration.loadConfiguration(messagesFile);

        // Configuration file
        configurationFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configurationFile.exists()) plugin.saveResource("config.yml", false);
        configurationConfiguration = YamlConfiguration.loadConfiguration(configurationFile);

        // Data file
        dataFile = new File(plugin.getDataFolder(), "data.yml");
        if (!dataFile.exists()) plugin.saveResource("data.yml", false);
        dataConfiguration = YamlConfiguration.loadConfiguration(dataFile);
    }

    // Messages file logic
    public FileConfiguration getMessages() {
        return messagesConfiguration;
    }

    public void reloadMessages() {
        //TODO: Clear the existing message object to remake them to make sure they get updated
        messagesConfiguration = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public void saveMessages() {
        try {
            messagesConfiguration.save(messagesFile);
        } catch (IOException exception) {
            Bukkit.getLogger().warning("Could not save messages.yml");
        }
    }

    public String getInvalidArgumentMessage() {
        return MessageUtils.colorTranslate(messagesConfiguration.getString("PluginMessages.InvalidArgument"));
    }

    public String getReloadedMessage() {
        return MessageUtils.colorTranslate(messagesConfiguration.getString("PluginMessages.Reloaded"));
    }

    public String getNoPermissionsMessage() {
        return MessageUtils.colorTranslate(messagesConfiguration.getString("PluginMessages.NoPermissions"));
    }

    public String getForceSendingMessage() {
        return MessageUtils.colorTranslate(messagesConfiguration.getString("PluginMessages.ForceSending"));
    }

    public String getSwitchedModeMessage(String mode) {
        return MessageUtils.colorTranslate(messagesConfiguration.getString("PluginMessages.SwitchedMode").replace("%state%", mode));
    }

    public String getMustBeAPlayerMessage() {
        return MessageUtils.colorTranslate(messagesConfiguration.getString("PluginMessages.MustBeAPlayer"));
    }

    public String getUnknownMessageMessage() {
        return MessageUtils.colorTranslate(messagesConfiguration.getString("PluginMessages.UnknownMessage"));
    }

    public String getCheckingUpdateMessage() {
        return MessageUtils.colorTranslate(messagesConfiguration.getString("PluginMessages.CheckingUpdate"));
    }

    public String getUpdateFoundMessage(String oldVersion, String newVersion) {
        return MessageUtils.colorTranslate(messagesConfiguration.getString("PluginMessages.CheckingUpdate").replace("%oldversion%", oldVersion)
                .replace("%newversion%", newVersion).replace("%url%", plugin.getUpdateChecker().getResourceURL()));
    }

    public String getUpdateNotFoundMessage() {
        return MessageUtils.colorTranslate(messagesConfiguration.getString("PluginMessages.UpdateNotFound"));
    }

    public String getChangedOrderMessage(String newOrder) {
        return MessageUtils.colorTranslate(messagesConfiguration.getString("PluginMessages.ChangedOrderMessage").replace("%order%", newOrder));
    }

    public String getMessageCreatedMessage() {
        return MessageUtils.colorTranslate(messagesConfiguration.getString("PluginMessages.MessageCreated"));
    }

    public String getMessageExistsMessage() {
        return MessageUtils.colorTranslate(messagesConfiguration.getString("PluginMessages.MessageExists"));
    }

    // Configuration file logic
    public FileConfiguration getConfigurationConfiguration() {
        return configurationConfiguration;
    }

    public void reloadConfiguration() {
        configurationConfiguration = YamlConfiguration.loadConfiguration(configurationFile);
    }

    public void saveConfiguration() {
        try {
            configurationConfiguration.save(configurationFile);
        } catch (IOException exception) {
            Bukkit.getLogger().warning("Could not save config.yml");
        }
    }

    // Data file logic
    public FileConfiguration getDataConfiguration() {
        return dataConfiguration;
    }

    public void reloadData() {
        dataConfiguration = YamlConfiguration.loadConfiguration(dataFile);
    }

    public void saveData() {
        try {
            dataConfiguration.save(dataFile);
        } catch (IOException exception) {
            Bukkit.getLogger().warning("Could not save data.yml");
        }
    }

    public boolean isAutocastEnabled() {
        return dataConfiguration.getBoolean("enabled");
    }

    // Bulk logic
    // TODO: Make sure this works
    public void reloadAll() {
        reloadConfiguration();
        reloadData();
        reloadMessages();
        plugin.reloadMessageHandler();
    }
}

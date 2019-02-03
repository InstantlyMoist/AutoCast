package me.kyllian.autocast.utils;

import me.kyllian.autocast.AutoCastPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Constructor;
import java.util.List;

public class Title {

    private AutoCastPlugin plugin;

    public Title(AutoCastPlugin plugin) {
        this.plugin = plugin;
    }

    public void sendTitle(Player player, Integer fadeIn, Integer fadeOut, Integer stay, List<String> titles, int duration) {
        int durationSingle = duration / titles.size();
        new BukkitRunnable() {
            int indexNumber = 0;
            public void run() {
                if (indexNumber == titles.size()) {
                    cancel();
                    return;
                }
                String[] split = titles.get(indexNumber++).split(";");
                sendTitle(player, fadeIn, fadeOut, stay, split[0], split[1]);
            }
        }.runTaskTimerAsynchronously(plugin, 0, durationSingle);
    }

    public void sendTitle(Player player, Integer fadeIn, Integer fadeOut, Integer stay, String title, String subtitle) {
        sendPacket(player, createTimesPacket(player, fadeIn, fadeOut, stay, title, subtitle));
        sendPacket(player, createTitlePacket(player, fadeIn, fadeOut, stay, title, subtitle, false));
        sendPacket(player, createTimesPacket(player, fadeIn, fadeOut, stay, title, subtitle));
        sendPacket(player, createTitlePacket(player, fadeIn, fadeOut, stay, title, subtitle, true));
    }

    public Object createTimesPacket(Player player, Integer fadeIn, Integer fadeOut, Integer stay,  String title, String subtitle) {
        if (title != null) title = MessageUtils.prepareMessage(player, title);
        if (subtitle != null) subtitle = MessageUtils.prepareMessage(player, subtitle);

        try {
            // Times packet
            Object object = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TIMES").get((Object) null);
            Object titleObject = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[]{String.class}).invoke((Object) null, new Object[]{"{\"text\":\"" + title + "\"}"});
            Constructor subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(new Class[]{getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE});
            Object titlePacket = subtitleConstructor.newInstance(new Object[]{object, titleObject, fadeIn, stay, fadeOut});
            return titlePacket;

        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }

    }

    public Object createTitlePacket(Player player, Integer fadeIn, Integer fadeOut, Integer stay,  String title, String subtitle, boolean isSubtitle) {
        if (title != null) title = MessageUtils.prepareMessage(player, title);
        if (subtitle != null) subtitle = MessageUtils.prepareMessage(player, subtitle);

        try {
            String finalString = isSubtitle ? subtitle : title;
            // Times packet
            Object object = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField(isSubtitle ? "SUBTITLE" : "TITLE").get((Object) null);
            Object titleObject = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[]{String.class}).invoke((Object) null, new Object[]{"{\"text\":\"" + finalString + "\"}"});
            Constructor subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(new Class[]{getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE});
            Object titlePacket = subtitleConstructor.newInstance(new Object[]{object, titleObject, fadeIn, stay, fadeOut});
            return titlePacket;

        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public static Class<?> getNMSClass(String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (ClassNotFoundException exc) {
            exc.printStackTrace();
            return null;
        }
    }

    public void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object connection = handle.getClass().getField("playerConnection").get(handle);
            connection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(connection, packet);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}

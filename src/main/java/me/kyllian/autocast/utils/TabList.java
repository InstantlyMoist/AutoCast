package me.kyllian.autocast.utils;

import me.kyllian.autocast.AutoCastPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class TabList {

    private AutoCastPlugin plugin;

    public TabList(AutoCastPlugin plugin) {
        this.plugin = plugin;
    }

    public Class<?> getNmsClass(String nmsClassName) throws ClassNotFoundException {
        return Class.forName("net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + "." + nmsClassName);
    }

    public void sendTabList(Player player, String headerText, String footerText) {
        try {
            Object header = getNmsClass("ChatComponentText").getConstructor(new Class[] { String.class }).newInstance(new Object[] { MessageUtils.prepareMessage(player, headerText) });
            Object footer = getNmsClass("ChatComponentText").getConstructor(new Class[] { String.class }).newInstance(new Object[] { MessageUtils.prepareMessage(player, footerText) });

            Object ppoplhf = getNmsClass("PacketPlayOutPlayerListHeaderFooter").getConstructor(new Class[0]).newInstance(new Object[0]);

            Field fa = ppoplhf.getClass().getDeclaredField("a");
            fa.setAccessible(true);
            fa.set(ppoplhf, header);
            Field fb = ppoplhf.getClass().getDeclaredField("b");
            fb.setAccessible(true);
            fb.set(ppoplhf, footer);

            Object nmsp = player.getClass().getMethod("getHandle", new Class[0]).invoke(player, new Object[0]);
            Object pcon = nmsp.getClass().getField("playerConnection").get(nmsp);

            pcon.getClass().getMethod("sendPacket", new Class[] { getNmsClass("Packet") }).invoke(pcon, new Object[] { ppoplhf });
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}

package me.kyllian.autocast.utils;

import me.kyllian.autocast.AutoCastPlugin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Method;
import java.util.List;

public class Book {

    private AutoCastPlugin plugin;
    private boolean initialized = false;
    private Method getHandle;
    private Method openBook;

    public Book(AutoCastPlugin plugin) {
        this.plugin = plugin;
        try {
            getHandle = ReflectionUtils.getMethod("CraftPlayer", ReflectionUtils.PackageType.CRAFTBUKKIT_ENTITY, "getHandle");
            openBook = ReflectionUtils.getMethod("EntityPlayer", ReflectionUtils.PackageType.MINECRAFT_SERVER, "a", ReflectionUtils.PackageType.MINECRAFT_SERVER.getClass("ItemStack"), ReflectionUtils.PackageType.MINECRAFT_SERVER.getClass("EnumHand"));
            initialized = true;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void openBook(ItemStack item, Player player) {
        new BukkitRunnable() {
            public void run() {
                ItemStack held = player.getInventory().getItemInMainHand();
                try {
                    player.getInventory().setItemInMainHand(item);
                    sendPacket(item, player);
                } catch (Exception exception) {
                    exception.printStackTrace();
                    initialized = false;
                }
                player.getInventory().setItemInMainHand(held);
            }
        }.runTaskLater(plugin, 1);
        if (!initialized) return;
    }

    private void sendPacket(ItemStack i, Player p) throws ReflectiveOperationException {
        Object entityplayer = getHandle.invoke(p);
        Class<?> enumHand = ReflectionUtils.PackageType.MINECRAFT_SERVER.getClass("EnumHand");
        Object[] enumArray = enumHand.getEnumConstants();
        openBook.invoke(entityplayer, getItemStack(i), enumArray[0]);
    }

    public Object getItemStack(ItemStack item) {
        try {
            Method asNMSCopy = ReflectionUtils.getMethod(ReflectionUtils.PackageType.CRAFTBUKKIT_INVENTORY.getClass("CraftItemStack"), "asNMSCopy", ItemStack.class);
            return asNMSCopy.invoke(ReflectionUtils.PackageType.CRAFTBUKKIT_INVENTORY.getClass("CraftItemStack"), item);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

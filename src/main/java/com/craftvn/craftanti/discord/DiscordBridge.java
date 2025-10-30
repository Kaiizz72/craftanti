package com.craftvn.craftanti.discord;

import com.craftvn.craftanti.CraftAnti;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.logging.Level;

public class DiscordBridge {
    private final CraftAnti plugin;
    private boolean hooked = false;
    private String channelKey;
    private boolean embed;
    private int minVl;

    public DiscordBridge(CraftAnti plugin) { this.plugin = plugin; }

    public void tryHook() {
        this.channelKey = plugin.getConfig().getString("discord.channel", "anticheat");
        this.embed = plugin.getConfig().getBoolean("discord.embed", true);
        this.minVl = plugin.getConfig().getInt("discord.min_vl_to_send", 3);
        if (!plugin.getConfig().getBoolean("discord.enabled", true)) return;

        Plugin dsrv = Bukkit.getPluginManager().getPlugin("DiscordSRV");
        if (dsrv != null && dsrv.isEnabled()) {
            hooked = true;
            plugin.getLogger().info("[Discord] Hooked into DiscordSRV. Channel key=" + channelKey);
        } else {
            plugin.getLogger().info("[Discord] DiscordSRV not found; running without Discord alerts.");
        }
    }

    public boolean isHooked() { return hooked; }

    public void sendDetection(String player, String checkName, int vl) {
        if (!hooked || vl < minVl) return;
        try {
            Class<?> discordSrvClazz = Class.forName("github.scarsz.discordsrv.DiscordSRV");
            Method getPlugin = discordSrvClazz.getMethod("getPlugin");
            Object dsrv = getPlugin.invoke(null);
            Method getDest = dsrv.getClass().getMethod("getDestinationTextChannelForGameChannelName", String.class);
            Object channel = getDest.invoke(dsrv, channelKey);
            if (channel == null) return;

            String raw = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&',
                    "&6[CraftAnti] &e" + player + " &7vi phạm &c" + checkName + " &7(VL &c" + vl + "&7)"));

            Method sendMessage = channel.getClass().getMethod("sendMessage", CharSequence.class);
            Object restAction = sendMessage.invoke(channel, raw);
            Method queue = restAction.getClass().getMethod("queue");
            queue.invoke(restAction);
        } catch (Throwable t) {
            plugin.getLogger().log(Level.WARNING, "[Discord] Failed to send detection message: " + t.getMessage());
        }
    }
}

package com.craftvn.craftanti.manager;

import com.craftvn.craftanti.CraftAnti;
import com.craftvn.craftanti.util.Color;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PunishManager {
    private final CraftAnti plugin;
    private final Map<UUID, Integer> kickCounts = new HashMap<>();
    private final Map<UUID, Integer> nextBanMinutes = new HashMap<>();

    private final int kicksBeforeBan;
    private final int startBanMinutes;
    private final int incrementMinutes;

    public PunishManager(CraftAnti plugin) {
        this.plugin = plugin;
        this.kicksBeforeBan = plugin.getConfig().getInt("punishments.kicks-before-ban", 3);
        this.startBanMinutes = plugin.getConfig().getInt("punishments.start-ban-minutes", 5);
        this.incrementMinutes = plugin.getConfig().getInt("punishments.increment-minutes", 1);
    }

    public void punishHack(Player p, String reason) {
        // if has bypass -> ignore
        if (p.hasPermission(plugin.getConfig().getString("safeguards.bypass-perm", "craftanti.bypass"))) return;

        int kicks = kickCounts.getOrDefault(p.getUniqueId(), 0) + 1;
        kickCounts.put(p.getUniqueId(), kicks);

        if (kicks >= kicksBeforeBan) {
            int minutes = nextBanMinutes.getOrDefault(p.getUniqueId(), startBanMinutes);
            ban(p, reason, minutes);
            kickCounts.put(p.getUniqueId(), 0);
            nextBanMinutes.put(p.getUniqueId(), minutes + incrementMinutes);
        } else {
            kick(p, reason + " (" + kicks + "/" + kicksBeforeBan + ")");
        }
    }

    public void kick(Player p, String reason) {
        String msg = plugin.getConfig().getString("messages.kick", "&cBạn đã bị &4KICK &cvì hack (&e%reason%&c)!");
        msg = Color.colorize(msg.replace("%reason%", reason));
        alertStaff(p.getName(), reason, "kick");
        try { p.kickPlayer(msg); } catch (Throwable ignored) {}
    }

    public void ban(Player p, String reason, int minutes) {
        String msg = plugin.getConfig().getString("messages.ban", "&cBạn đã bị &4BAN &cvì hack (&e%reason%&c)!\\n&aThời hạn: &e%time% phút");
        msg = Color.colorize(msg.replace("%reason%", reason).replace("%time%", String.valueOf(minutes)));
        alertStaff(p.getName(), reason + " ("+minutes+"m)", "ban");
        try {
            Bukkit.getBanList(BanList.Type.NAME).addBan(p.getName(), "CraftAnti: " + reason, Date.from(Instant.now().plus(minutes, ChronoUnit.MINUTES)), "CraftAnti");
        } catch (Throwable ignored) {}
        try { p.kickPlayer(msg); } catch (Throwable ignored) {}
    }

    private void alertStaff(String player, String reason, String type) {
        String alert = plugin.getConfig().getString("messages.alert", "&6[CraftAnti] &e%player% &cbị xử lý vì: &f%reason%");
        alert = Color.colorize(alert.replace("%player%", player).replace("%reason%", reason));
        String perm = plugin.getConfig().getString("safeguards.alert-perm", "craftanti.alert");
        Bukkit.getOnlinePlayers().forEach(st -> {
            if (st.hasPermission(perm)) st.sendMessage(alert);
        });
        plugin.getLogger().warning("[CraftAnti] " + type.toUpperCase() + " -> " + player + " : " + reason);
    }
}

package com.craftvn.craftanti.checks;

import com.craftvn.craftanti.CraftAnti;
import com.craftvn.craftanti.manager.PunishManager;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ElytraFollowCheck {
    private final CraftAnti plugin;
    private final PunishManager punish;
    private final Map<UUID, Deque<Double>> angleBuf = new HashMap<>();
    private final Map<UUID, Vector> lastDir = new HashMap<>();

    public ElytraFollowCheck(CraftAnti plugin) {
        this.plugin = plugin;
        this.punish = plugin.getPunishManager();
    }

    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (!plugin.getConfig().getBoolean("checks.java.elytrafollow.enabled", true)) return;
        if (!p.isGliding()) {
            angleBuf.remove(p.getUniqueId());
            lastDir.remove(p.getUniqueId());
            return;
        }

        int sampleRate = plugin.getConfig().getInt("checks.java.elytrafollow.sample-rate-ticks", 2);
        // simple tick gating via modulo of player ticks lived
        if ((p.getTicksLived() % sampleRate) != 0) return;

        Vector look = p.getLocation().getDirection().normalize();
        Vector prev = lastDir.get(p.getUniqueId());
        if (prev != null) {
            double dot = Math.max(-1, Math.min(1, look.dot(prev)));
            double angle = Math.toDegrees(Math.acos(dot));
            Deque<Double> buf = angleBuf.computeIfAbsent(p.getUniqueId(), k -> new ArrayDeque<>());
            buf.addLast(angle);
            int max = plugin.getConfig().getInt("checks.java.elytrafollow.buffer-size", 30);
            while (buf.size() > max) buf.removeFirst();

            if (buf.size() >= 10) {
                double sum = 0; for (double a: buf) sum += a;
                double mean = sum / buf.size();
                double ss = 0; for (double a: buf) ss += (a-mean)*(a-mean);
                double std = Math.sqrt(ss / buf.size());

                double meanThr = plugin.getConfig().getDouble("checks.java.elytrafollow.mean-angle-threshold", 8.0);
                double stdThr = plugin.getConfig().getDouble("checks.java.elytrafollow.stddev-angle-threshold", 3.0);
                if (mean <= meanThr && std <= stdThr) {
                    punish.punishHack(p, "ElytraFollow mean="+String.format(\"%.2f\",mean)+\" sd=\"+String.format(\"%.2f\",std));
                }
            }
        }
        lastDir.put(p.getUniqueId(), look);
    }
}

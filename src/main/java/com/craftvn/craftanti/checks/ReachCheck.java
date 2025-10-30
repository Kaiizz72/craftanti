package com.craftvn.craftanti.checks;

import com.craftvn.craftanti.CraftAnti;
import com.craftvn.craftanti.manager.PunishManager;
import com.craftvn.craftanti.util.BedrockUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

public class ReachCheck {
    private final CraftAnti plugin;
    private final PunishManager punish;

    public ReachCheck(CraftAnti plugin) {
        this.plugin = plugin;
        this.punish = plugin.getPunishManager();
    }

    private double distancePointToAABB(Vector point, BoundingBox bb) {
        double dx = Math.max(Math.max(bb.getMinX() - point.getX(), 0.0), point.getX() - bb.getMaxX());
        double dy = Math.max(Math.max(bb.getMinY() - point.getY(), 0.0), point.getY() - bb.getMaxY());
        double dz = Math.max(Math.max(bb.getMinZ() - point.getZ(), 0.0), point.getZ() - bb.getMaxZ());
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public void onAttack(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) return;
        Entity target = e.getEntity();
        Player attacker = (Player) e.getDamager();

        double dist;
        try {
            dist = distancePointToAABB(attacker.getEyeLocation().toVector(), target.getBoundingBox());
        } catch (Throwable t) {
            // fallback
            dist = attacker.getLocation().distance(target.getLocation());
        }

        if (BedrockUtil.isBedrock(attacker)) {
            if (!plugin.getConfig().getBoolean("checks.pe.reach.enabled", true)) return;
            double max = plugin.getConfig().getDouble("checks.pe.reach.max", 3.2);
            if (dist > max) {
                e.setCancelled(true);
                punish.punishHack(attacker, "PE-Reach (" + String.format("%.2f", dist) + " > " + max + ")");
            }
        } else {
            if (!plugin.getConfig().getBoolean("checks.java.reach.enabled", true)) return;
            double max = plugin.getConfig().getDouble("checks.java.reach.max", 3.6);
            if (dist > max) {
                e.setCancelled(true);
                punish.punishHack(attacker, "Java-Reach (" + String.format("%.2f", dist) + " > " + max + ")");
            }
        }
    }
}

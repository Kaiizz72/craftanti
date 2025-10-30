package com.craftvn.craftanti.checks;

import com.craftvn.craftanti.CraftAnti;
import com.craftvn.craftanti.manager.PunishManager;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.*;

public class AimAssistCheck {
    private final CraftAnti plugin;
    private final PunishManager punish;
    private final Map<UUID, Deque<Float>> yawHistory = new HashMap<>();

    public AimAssistCheck(CraftAnti plugin) {
        this.plugin = plugin;
        this.punish = plugin.getPunishManager();
    }

    public void onAttack(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) return;
        Player p = (Player) e.getDamager();
        if (!plugin.getConfig().getBoolean("checks.java.aimassist.enabled", true)) return;

        Deque<Float> hist = yawHistory.computeIfAbsent(p.getUniqueId(), k -> new ArrayDeque<>());
        hist.addLast(p.getLocation().getYaw());
        int max = plugin.getConfig().getInt("checks.java.aimassist.history", 10);
        while (hist.size() > max) hist.removeFirst();

        if (hist.size() >= 6) {
            double avg = hist.stream().mapToDouble(Float::doubleValue).average().orElse(0);
            double var = 0;
            for (float f : hist) var += (f - avg) * (f - avg);
            var /= hist.size();
            double minVar = plugin.getConfig().getDouble("checks.java.aimassist.yaw-variance-min", 1.5);
            if (var < minVar) {
                e.setCancelled(true);
                punish.punishHack(p, "AimAssist var=" + String.format("%.2f", var));
            }
        }
    }
}

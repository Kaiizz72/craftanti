package com.craftvn.craftanti.checks;

import com.craftvn.craftanti.CraftAnti;
import com.craftvn.craftanti.manager.PunishManager;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public class NoFallCheck {
    private final CraftAnti plugin;
    private final PunishManager punish;

    public NoFallCheck(CraftAnti plugin) {
        this.plugin = plugin;
        this.punish = plugin.getPunishManager();
    }

    public void onFall(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player p = (Player) e.getEntity();
        if (!plugin.getConfig().getBoolean("checks.java.nofall.enabled", true)) return;

        if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
            double min = plugin.getConfig().getDouble("checks.java.nofall.min-fall-distance", 4.0);
            if (p.getFallDistance() > min && e.getDamage() == 0) {
                punish.punishHack(p, "NoFall (fallDist=" + String.format("%.1f", p.getFallDistance()) + ")");
            }
        }
    }
}

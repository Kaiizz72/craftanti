package com.craftvn.craftanti.checks;

import com.craftvn.craftanti.CraftAnti;
import com.craftvn.craftanti.manager.PunishManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

public class NoWebCheck {
    private final CraftAnti plugin;
    private final PunishManager punish;

    public NoWebCheck(CraftAnti plugin) {
        this.plugin = plugin;
        this.punish = plugin.getPunishManager();
    }

    public void onMove(PlayerMoveEvent e) {
        if (!plugin.getConfig().getBoolean("checks.java.noweb.enabled", true)) return;
        Player p = e.getPlayer();

        Material m = e.getTo().getBlock().getType();
        if (m == Material.COBWEB || m == Material.HONEY_BLOCK || m == Material.SOUL_SAND) {
            double dx = e.getTo().getX() - e.getFrom().getX();
            double dz = e.getTo().getZ() - e.getFrom().getZ();
            double speed = Math.sqrt(dx*dx + dz*dz);
            double max = plugin.getConfig().getDouble("checks.java.noweb.max-speed-in-web", 0.25);
            if (speed > max) {
                e.setTo(e.getFrom());
                punish.punishHack(p, "NoWeb speed=" + String.format("%.2f", speed));
            }
        }
    }
}

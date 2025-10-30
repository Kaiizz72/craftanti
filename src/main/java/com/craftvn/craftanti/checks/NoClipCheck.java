package com.craftvn.craftanti.checks;

import com.craftvn.craftanti.CraftAnti;
import com.craftvn.craftanti.manager.PunishManager;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

public class NoClipCheck {
    private final CraftAnti plugin;
    private final PunishManager punish;

    public NoClipCheck(CraftAnti plugin) {
        this.plugin = plugin;
        this.punish = plugin.getPunishManager();
    }

    public void onMove(PlayerMoveEvent e) {
        if (!plugin.getConfig().getBoolean("checks.java.noclip.enabled", true)) return;
        Player p = e.getPlayer();
        if (p.getGameMode() == GameMode.SPECTATOR || p.getGameMode() == GameMode.CREATIVE) return;

        if (e.getTo().getBlock().getType().isSolid()) {
            e.setTo(e.getFrom());
            double pts = plugin.getConfig().getDouble("checks.java.noclip.violation-points", 5.0);
            punish.punishHack(p, "NoClip (inside solid)");
        }
    }
}

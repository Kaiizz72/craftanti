package com.craftvn.craftanti.checks;

import com.craftvn.craftanti.CraftAnti;
import com.craftvn.craftanti.manager.PunishManager;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TimerCheck {
    private final CraftAnti plugin;
    private final PunishManager punish;
    private final Map<UUID, Integer> moveCounts = new HashMap<>();
    private long lastTick = System.currentTimeMillis();

    public TimerCheck(CraftAnti plugin) {
        this.plugin = plugin;
        this.punish = plugin.getPunishManager();
    }

    public void onMove(PlayerMoveEvent e) {
        if (!plugin.getConfig().getBoolean("checks.java.timer.enabled", true)) return;
        Player p = e.getPlayer();

        moveCounts.put(p.getUniqueId(), moveCounts.getOrDefault(p.getUniqueId(), 0) + 1);
        long now = System.currentTimeMillis();
        if (now - lastTick >= 1000) {
            int moves = moveCounts.getOrDefault(p.getUniqueId(), 0);
            moveCounts.put(p.getUniqueId(), 0);
            lastTick = now;

            int max = plugin.getConfig().getInt("checks.java.timer.max-moves-per-second", 25);
            if (moves > max) {
                punish.punishHack(p, "Timer (" + moves + " moves/s)");
            }
        }
    }
}

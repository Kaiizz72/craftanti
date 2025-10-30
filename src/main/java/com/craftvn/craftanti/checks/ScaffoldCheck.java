package com.craftvn.craftanti.checks;

import com.craftvn.craftanti.CraftAnti;
import com.craftvn.craftanti.manager.PunishManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ScaffoldCheck {
    private final CraftAnti plugin;
    private final PunishManager punish;
    private final Map<UUID, Long> lastPlace = new HashMap<>();

    public ScaffoldCheck(CraftAnti plugin) {
        this.plugin = plugin;
        this.punish = plugin.getPunishManager();
    }

    public void onPlace(BlockPlaceEvent e) {
        if (!plugin.getConfig().getBoolean("checks.java.scaffold.enabled", true)) return;
        Player p = e.getPlayer();
        Location under = p.getLocation().clone().subtract(0, 1, 0).getBlock().getLocation();
        if (e.getBlockPlaced().getLocation().equals(under)) {
            long now = System.currentTimeMillis();
            long last = lastPlace.getOrDefault(p.getUniqueId(), 0L);
            lastPlace.put(p.getUniqueId(), now);
            long delta = now - last;
            int minDelay = plugin.getConfig().getInt("checks.java.scaffold.min-delay-ms", 150);
            if (last > 0 && delta < minDelay) {
                punish.punishHack(p, "Scaffold (" + delta + "ms)");
            }
        }
    }
}

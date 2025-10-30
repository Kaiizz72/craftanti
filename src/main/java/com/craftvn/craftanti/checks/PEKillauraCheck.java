package com.craftvn.craftanti.checks;

import com.craftvn.craftanti.CraftAnti;
import com.craftvn.craftanti.manager.PunishManager;
import com.craftvn.craftanti.util.BedrockUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.*;

public class PEKillauraCheck {
    private final CraftAnti plugin;
    private final PunishManager punish;
    private final Map<UUID, Deque<Long>> clicks = new HashMap<>();

    public PEKillauraCheck(CraftAnti plugin) {
        this.plugin = plugin;
        this.punish = plugin.getPunishManager();
    }

    public void onAttack(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) return;
        Player p = (Player) e.getDamager();
        if (!BedrockUtil.isBedrock(p)) return;
        if (!plugin.getConfig().getBoolean("checks.pe.killaura.enabled", true)) return;

        long now = System.currentTimeMillis();
        Deque<Long> dq = clicks.computeIfAbsent(p.getUniqueId(), k -> new ArrayDeque<>());
        dq.addLast(now);
        while (!dq.isEmpty() && now - dq.peekFirst() > 1000) dq.removeFirst();

        int cps = dq.size();
        int max = plugin.getConfig().getInt("checks.pe.killaura.max-cps", 15);
        if (cps > max) {
            e.setCancelled(true);
            punish.punishHack(p, "PE-Killaura CPS=" + cps);
        }
    }
}

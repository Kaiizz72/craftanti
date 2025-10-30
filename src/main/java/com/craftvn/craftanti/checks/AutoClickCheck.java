package com.craftvn.craftanti.checks;

import com.craftvn.craftanti.CraftAnti;
import com.craftvn.craftanti.manager.PunishManager;
import com.craftvn.craftanti.util.BedrockUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.*;

public class AutoClickCheck {
    private final CraftAnti plugin;
    private final PunishManager punish;
    private final Map<UUID, Deque<Long>> clicks = new HashMap<>();

    public AutoClickCheck(CraftAnti plugin) {
        this.plugin = plugin;
        this.punish = plugin.getPunishManager();
    }

    public void onAttack(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) return;
        Player p = (Player) e.getDamager();

        // Skip PE autoclick strict
        if (BedrockUtil.isBedrock(p)) return;
        if (!plugin.getConfig().getBoolean("checks.java.autoclick.enabled", true)) return;

        long now = System.currentTimeMillis();
        Deque<Long> dq = clicks.computeIfAbsent(p.getUniqueId(), k -> new ArrayDeque<>());
        dq.addLast(now);
        while (!dq.isEmpty() && now - dq.peekFirst() > 1000) dq.removeFirst();

        int cps = dq.size();
        int maxCps = plugin.getConfig().getInt("checks.java.autoclick.max-cps", 20);
        if (cps > maxCps) {
            e.setCancelled(true);
            punish.punishHack(p, "AutoClick CPS=" + cps);
            return;
        }

        // CoV check
        if (dq.size() >= 6) {
            List<Long> list = new ArrayList<>(dq);
            List<Long> intervals = new ArrayList<>();
            for (int i = 1; i < list.size(); i++) intervals.add(list.get(i) - list.get(i - 1));
            double mean = intervals.stream().mapToDouble(Long::doubleValue).average().orElse(0);
            if (mean > 0) {
                double sd = Math.sqrt(intervals.stream().mapToDouble(v -> (v - mean) * (v - mean)).sum() / intervals.size());
                double cov = sd / mean;
                double covThr = plugin.getConfig().getDouble("checks.java.autoclick.cov-threshold", 0.06);
                if (cov < covThr) {
                    e.setCancelled(true);
                    punish.punishHack(p, "AutoClick CoV=" + String.format("%.3f", cov));
                }
            }
        }
    }
}

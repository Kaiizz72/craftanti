package com.craftvn.craftanti.violation;

import com.craftvn.craftanti.CraftAnti;
import com.craftvn.craftanti.platform.PlatformUtil;
import com.craftvn.craftanti.util.MathUtil;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProtocolListener {
    private final CraftAnti plugin;
    private ProtocolManager manager;
    private final Map<UUID, Integer> cps = new HashMap<>();
    private final Map<UUID, Integer> targetsThisSec = new HashMap<>();
    private final Map<UUID, Integer> lastTargetId = new HashMap<>();
    private int taskId = -1;

    public ProtocolListener(CraftAnti plugin) { this.plugin = plugin; }

    public void tryHook() {
        try { manager = ProtocolLibrary.getProtocolManager(); }
        catch (Throwable t) { plugin.getLogger().warning("[CraftAnti] ProtocolLib not present? Some checks disabled."); return; }

        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> { cps.clear(); targetsThisSec.clear(); }, 20L, 20L);

        manager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Client.USE_ENTITY) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                if (event.isCancelled()) return;
                Player p = event.getPlayer();
                PacketContainer pkt = event.getPacket();

                try {
                    Enum<?> action = pkt.getEnumModifier(Enum.class, 0).read(0);
                    if (!action.name().equalsIgnoreCase("ATTACK")) return;
                } catch (Throwable ignored) { }

                int targetId = -1;
                try { targetId = pkt.getIntegers().read(0); } catch (Throwable ignored) { }
                Entity target = null;
                if (targetId != -1) target = pkt.getEntityModifier(p.getWorld()).read(0);

                boolean bedrock = PlatformUtil.isBedrock(p);

                cps.put(p.getUniqueId(), cps.getOrDefault(p.getUniqueId(), 0) + 1);
                int prev = lastTargetId.getOrDefault(p.getUniqueId(), -1);
                if (targetId != -1 && prev != -1 && prev != targetId) targetsThisSec.put(p.getUniqueId(), targetsThisSec.getOrDefault(p.getUniqueId(), 0) + 1);
                if (targetId != -1) lastTargetId.put(p.getUniqueId(), targetId);

                String base = bedrock ? "bedrock.killaura" : "java.killaura";
                int maxCps = plugin.getConfig().getInt(base + ".max_cps", bedrock ? 16 : 14);
                int maxTargets = plugin.getConfig().getInt(base + ".max_targets_per_sec", bedrock ? 4 : 3);
                int curCps = cps.getOrDefault(p.getUniqueId(), 0);
                int curTargets = targetsThisSec.getOrDefault(p.getUniqueId(), 0);

                if (curCps > maxCps) plugin.checks().addViolation(p, "KillAura(CPS)", 1);
                if (curTargets > maxTargets) plugin.checks().addViolation(p, "KillAura(MultiTarget)", 1);

                if (target != null) {
                    double angle = MathUtil.angleBetween(p, target);
                    double maxFlick = plugin.getConfig().getDouble(base + ".max_flick_deg", bedrock ? 110 : 95);
                    if (angle > maxFlick) plugin.checks().addViolation(p, "KillAura(FlickAngle)", 1);

                    boolean requireLos = plugin.getConfig().getBoolean(base + ".require_los", true);
                    if (requireLos && !p.hasLineOfSight(target)) plugin.checks().addViolation(p, "Hitbox(NoLOS)", 1);

                    if (bedrock && plugin.getConfig().getBoolean("bedrock.hitbox.enabled", true)) {
                        double maxOff = plugin.getConfig().getDouble("bedrock.hitbox.max_angle_off", 120.0);
                        if (angle > maxOff) plugin.checks().addViolation(p, "Hitbox(PE-OffAngle)", 1);
                    }
                }
            }
        });
        plugin.getLogger().info("ProtocolLib checks enabled (KillAura/Hitbox).");
    }

    public void shutdown() {
        cps.clear(); targetsThisSec.clear(); lastTargetId.clear();
        if (taskId != -1) Bukkit.getScheduler().cancelTask(taskId);
    }
}

package com.craftvn.craftanti.movement;

import com.craftvn.craftanti.CraftAnti;
import com.craftvn.craftanti.platform.PlatformUtil;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

public class SpeedFlyListener implements Listener {
    public SpeedFlyListener(CraftAnti plugin) { }

    private boolean isExempt(Player p) {
        if (p == null) return true;
        if (p.getGameMode() == GameMode.CREATIVE || p.getGameMode() == GameMode.SPECTATOR) return true;
        if (p.isInsideVehicle()) return true;
        return false;
    }

    private boolean isInLiquid(Player p) {
        Block b = p.getLocation().getBlock();
        Material m = b.getType();
        return m == Material.WATER || m == Material.KELP || m == Material.KELP_PLANT || m == Material.SEAGRASS || m == Material.TALL_SEAGRASS;
    }

    private boolean isOnClimbable(Player p) {
        Material m = p.getLocation().getBlock().getType();
        return m == Material.LADDER || m == Material.VINE || m == Material.TWISTING_VINES || m == Material.WEEPING_VINES;
    }

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (isExempt(p)) return;

        boolean bedrock = PlatformUtil.isBedrock(p);
        String speedBase = (bedrock ? "bedrock" : "java") + ".speed.";
        String flyBase   = (bedrock ? "bedrock" : "java") + ".fly.";

        // AntiSpeed (horizontal)
        double dx = e.getTo().getX() - e.getFrom().getX();
        double dz = e.getTo().getZ() - e.getFrom().getZ();
        double distXZ = Math.hypot(dx, dz);
        double bps = distXZ * 20.0;

        double maxBps = CraftAnti.get().getConfig().getDouble(speedBase + "max_horizontal_bps", bedrock ? 8.5 : 7.2);
        double graceWithPotion = CraftAnti.get().getConfig().getDouble(speedBase + "grace_bps_with_speed_potion", bedrock ? 1.5 : 1.2);
        boolean hasSpeed = p.hasPotionEffect(PotionEffectType.SPEED);

        double allow = maxBps + (hasSpeed ? graceWithPotion : 0.0);
        if (!p.isGliding() && p.isOnGround() && !isInLiquid(p) && !isOnClimbable(p)) {
            if (bps > allow) CraftAnti.get().checks().addViolation(p, "Speed(Horizontal)", 1);
        }

        // AntiSpeed (vertical)
        double dy = e.getTo().getY() - e.getFrom().getY();
        double yps = dy * 20.0;
        double maxYps = CraftAnti.get().getConfig().getDouble(speedBase + "max_vertical_yps", bedrock ? 10.0 : 8.5);
        if (!p.isGliding() && !isInLiquid(p)) {
            if (!p.hasPotionEffect(PotionEffectType.JUMP) && !p.hasPotionEffect(PotionEffectType.LEVITATION)) {
                if (Math.abs(yps) > maxYps) CraftAnti.get().checks().addViolation(p, "Speed(Vertical)", 1);
            }
        }

        // AntiFly (air time)
        if (CraftAnti.get().getConfig().getBoolean((bedrock ? "bedrock" : "java") + ".fly.enabled", true)) {
            int maxAirTicks = CraftAnti.get().getConfig().getInt(flyBase + "max_air_ticks", bedrock ? 45 : 35);
            boolean ignoreElytra = CraftAnti.get().getConfig().getBoolean(flyBase + "ignore_when_elytra", true);
            boolean ignoreLiquid = CraftAnti.get().getConfig().getBoolean(flyBase + "ignore_in_liquid", true);
            boolean ignoreLadder = CraftAnti.get().getConfig().getBoolean(flyBase + "ignore_on_ladder", true);

            boolean ok = true;
            if (ignoreElytra && p.isGliding()) ok = false;
            if (ignoreLiquid && isInLiquid(p)) ok = false;
            if (ignoreLadder && isOnClimbable(p)) ok = false;
            if (p.getAllowFlight()) ok = false;

            if (ok) {
                int air = p.getMetadata("craftanti_air").isEmpty() ? 0 : p.getMetadata("craftanti_air").get(0).asInt();
                if (!p.isOnGround()) {
                    air++;
                    p.setMetadata("craftanti_air", new org.bukkit.metadata.FixedMetadataValue(CraftAnti.get(), air));
                    if (air > maxAirTicks) CraftAnti.get().checks().addViolation(p, "Fly(AirTime)", 1);
                } else {
                    p.removeMetadata("craftanti_air", CraftAnti.get());
                }
            }
        }
    }
}

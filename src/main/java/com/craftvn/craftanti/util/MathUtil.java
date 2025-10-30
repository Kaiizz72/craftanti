package com.craftvn.craftanti.util;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public final class MathUtil {
    private MathUtil(){}
    public static double angleBetween(Player p, Entity target) {
        Vector look = p.getLocation().getDirection().normalize();
        Vector to = target.getLocation().toVector().subtract(p.getLocation().toVector()).normalize();
        double dot = look.dot(to);
        dot = Math.max(-1.0, Math.min(1.0, dot));
        return Math.toDegrees(Math.acos(dot));
    }
}

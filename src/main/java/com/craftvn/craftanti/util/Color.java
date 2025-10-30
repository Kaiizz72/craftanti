package com.craftvn.craftanti.util;

import org.bukkit.ChatColor;

public class Color {
    public static String colorize(String s) {
        return ChatColor.translateAlternateColorCodes('&', s == null ? "" : s);
    }
}

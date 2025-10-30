package com.craftvn.craftanti.util;

import org.bukkit.entity.Player;

public class BedrockUtil {
    public static boolean isBedrock(Player p) {
        // Simple heuristic: Floodgate players often have a '.' prefix (adjust if you use API)
        return p.getName().startsWith(".");
    }
}

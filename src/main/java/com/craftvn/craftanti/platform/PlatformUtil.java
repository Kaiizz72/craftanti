package com.craftvn.craftanti.platform;

import org.bukkit.entity.Player;
import java.util.UUID;

public final class PlatformUtil {
    private PlatformUtil() { }
    public static boolean isBedrock(Player p) {
        try {
            Class<?> apiClazz = Class.forName("org.geysermc.floodgate.api.FloodgateApi");
            Object api = apiClazz.getMethod("getInstance").invoke(null);
            Boolean result = (Boolean) api.getClass().getMethod("isFloodgatePlayer", UUID.class).invoke(api, p.getUniqueId());
            if (result != null) return result;
        } catch (Throwable ignored) { }
        try {
            Class<?> apiClazz = Class.forName("org.geysermc.geyser.api.GeyserApi");
            Object api = apiClazz.getMethod("api").invoke(null);
            Boolean result = (Boolean) api.getClass().getMethod("isBedrockPlayer", UUID.class).invoke(api, p.getUniqueId());
            if (result != null) return result;
        } catch (Throwable ignored) { }
        return false;
    }
}

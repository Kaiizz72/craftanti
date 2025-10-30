package com.craftvn.craftanti;

import com.craftvn.craftanti.command.CraftAntiCommand;
import com.craftvn.craftanti.listener.CombatListener;
import com.craftvn.craftanti.listener.MovementListener;
import com.craftvn.craftanti.manager.PunishManager;
import org.bukkit.plugin.java.JavaPlugin;
import com.craftvn.craftanti.movement.SpeedFlyListener;

public class CraftAnti extends JavaPlugin {

    private static CraftAnti instance;
    private PunishManager punishManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        punishManager = new PunishManager(this);

        getServer().getPluginManager().registerEvents(new CombatListener(this), this);
        getServer().getPluginManager().registerEvents(new MovementListener(this), this);

        getCommand("craftanti").setExecutor(new CraftAntiCommand(this));
        getServer().getPluginManager().registerEvents(new SpeedFlyListener(this), this);
        getLogger().info("CraftAnti enabled.");
    }

    @Override
    public void onDisable() {
        getServer().getPluginManager().registerEvents(new SpeedFlyListener(this), this);
        getLogger().info("CraftAnti disabled.");
    }

    public PunishManager getPunishManager() {
        return punishManager;
    }

    public static CraftAnti get() {
        return instance;
    }
}

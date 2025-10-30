package com.craftvn.craftanti.listener;

import com.craftvn.craftanti.CraftAnti;
import com.craftvn.craftanti.checks.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class MovementListener implements Listener {

    private final NoWebCheck noWebCheck;
    private final NoClipCheck noClipCheck;
    private final TimerCheck timerCheck;
    private final ElytraFollowCheck elytraFollowCheck;
    private final ScaffoldCheck scaffoldCheck;

    public MovementListener(CraftAnti plugin) {
        this.noWebCheck = new NoWebCheck(plugin);
        this.noClipCheck = new NoClipCheck(plugin);
        this.timerCheck = new TimerCheck(plugin);
        this.elytraFollowCheck = new ElytraFollowCheck(plugin);
        this.scaffoldCheck = new ScaffoldCheck(plugin);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        noWebCheck.onMove(e);
        noClipCheck.onMove(e);
        timerCheck.onMove(e);
        elytraFollowCheck.onMove(e);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        scaffoldCheck.onPlace(e);
    }
}

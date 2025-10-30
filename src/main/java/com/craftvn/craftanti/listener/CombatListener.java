package com.craftvn.craftanti.listener;

import com.craftvn.craftanti.CraftAnti;
import com.craftvn.craftanti.checks.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class CombatListener implements Listener {

    private final ReachCheck reachCheck;
    private final AutoClickCheck autoClickCheck;
    private final PEKillauraCheck peKillauraCheck;
    private final AimAssistCheck aimAssistCheck;
    private final NoFallCheck noFallCheck;

    public CombatListener(CraftAnti plugin) {
        this.reachCheck = new ReachCheck(plugin);
        this.autoClickCheck = new AutoClickCheck(plugin);
        this.peKillauraCheck = new PEKillauraCheck(plugin);
        this.aimAssistCheck = new AimAssistCheck(plugin);
        this.noFallCheck = new NoFallCheck(plugin);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        reachCheck.onAttack(e);
        autoClickCheck.onAttack(e);
        peKillauraCheck.onAttack(e);
        aimAssistCheck.onAttack(e);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        noFallCheck.onFall(e);
    }
}

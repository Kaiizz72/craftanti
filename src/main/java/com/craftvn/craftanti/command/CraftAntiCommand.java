package com.craftvn.craftanti.command;

import com.craftvn.craftanti.CraftAnti;
import com.craftvn.craftanti.util.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CraftAntiCommand implements CommandExecutor {
    private final CraftAnti plugin;

    public CraftAntiCommand(CraftAnti plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Color.colorize("&6/CraftAnti reload &7- reload config"));
            return true;
        }
        if ("reload".equalsIgnoreCase(args[0])) {
            plugin.reloadConfig();
            sender.sendMessage(Color.colorize("&a[CraftAnti] Reloaded config."));
            return true;
        }
        return true;
    }
}

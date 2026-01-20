package org.RisingSMP.factory.commands;

import org.RisingSMP.factory.Factory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FactoryConfigCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) return true;
        if (!player.isOp()) return true;

        if (args.length == 0) {
            player.sendMessage("§cUso: /factoryconfig <INTERMEDIATE|FINAL>");
            return true;
        }

        String type = args[0].toUpperCase();
        if (!type.equals("INTERMEDIATE") && !type.equals("FINAL")) {
            player.sendMessage("§cTipo non valido!");
            return true;
        }

        Factory.instance.factoryGUI.openGUI(player, type);
        return true;
    }
}
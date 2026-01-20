package org.RisingSMP.factory.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.RisingSMP.factory.items.FactoryItems;

public class GiveFactoryCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) return true;
        if (!player.isOp()) return true;

        if (args.length == 0) {
            player.sendMessage("§cUso corretto: /givefactory <input|belt|intermediate|final>");
            return true;
        }

        ItemStack item = switch (args[0].toLowerCase()) {
            case "input" -> FactoryItems.inputBlock();
            case "belt" -> FactoryItems.conveyor();
            case "intermediate" -> FactoryItems.intermediateFactory();
            case "final" -> FactoryItems.finalFactory();
            default -> null;
        };

        if (item == null) {
            player.sendMessage("§cTipo non valido!");
            return true;
        }

        player.getInventory().addItem(item);
        player.sendMessage("§aHai ricevuto il blocco: " + args[0]);
        return true;
    }
}
package org.RisingSMP.factory.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.RisingSMP.factory.items.FactoryItems;

public class GiveFactoryCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) return true;
        if (!player.isOp()) return true;

        if (args.length == 0) {
            player.sendMessage("§cUso corretto: /givefactory <input|belt|intermediate|final|material> [quantità]");
            player.sendMessage("§7Materiali disponibili: iron, gold, diamond, coal, redstone, emerald");
            return true;
        }

        ItemStack item = null;
        int amount = 1;
        
        // Controlla se è stato specificato un amount
        if (args.length >= 2) {
            try {
                amount = Integer.parseInt(args[1]);
                if (amount <= 0 || amount > 64) {
                    player.sendMessage("§cQuantità non valida! Usa un numero tra 1 e 64.");
                    return true;
                }
            } catch (NumberFormatException e) {
                player.sendMessage("§cQuantità non valida! Usa un numero valido.");
                return true;
            }
        }

        // Controlla se è un materiale base
        if (args[0].equalsIgnoreCase("iron")) {
            item = new ItemStack(Material.IRON_INGOT, amount);
        } else if (args[0].equalsIgnoreCase("gold")) {
            item = new ItemStack(Material.GOLD_INGOT, amount);
        } else if (args[0].equalsIgnoreCase("diamond")) {
            item = new ItemStack(Material.DIAMOND, amount);
        } else if (args[0].equalsIgnoreCase("coal")) {
            item = new ItemStack(Material.COAL, amount);
        } else if (args[0].equalsIgnoreCase("redstone")) {
            item = new ItemStack(Material.REDSTONE, amount);
        } else if (args[0].equalsIgnoreCase("emerald")) {
            item = new ItemStack(Material.EMERALD, amount);
        } else {
            // Controlla se è un blocco factory
            item = switch (args[0].toLowerCase()) {
                case "input" -> FactoryItems.inputBlock();
                case "belt" -> FactoryItems.conveyor();
                case "intermediate" -> FactoryItems.intermediateFactory();
                case "final" -> FactoryItems.finalFactory();
                case "generator" -> FactoryItems.GENERATOR;
                case "refinery" -> FactoryItems.REFINERY;
                case "oil" -> FactoryItems.OIL_BLOCK;
                default -> null;
            };
        }

        if (item == null) {
            player.sendMessage("§cTipo non valido!");
            player.sendMessage("§7Tipi validi: input, belt, intermediate, final, generator, refinery, oil");
            player.sendMessage("§7Materiali: iron, gold, diamond, coal, redstone, emerald");
            return true;
        }

        // Imposta la quantità corretta
        item.setAmount(amount);
        
        player.getInventory().addItem(item);
        
        // Mostra nome appropriato
        String itemName = item.getItemMeta() != null ? 
            item.getItemMeta().getDisplayName() : 
            item.getType().name().toLowerCase().replace("_", " ");
        
        player.sendMessage("§aHai ricevuto: " + amount + "x " + itemName);
        return true;
    }
}
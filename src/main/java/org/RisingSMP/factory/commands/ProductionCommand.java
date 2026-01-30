package org.RisingSMP.factory.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.RisingSMP.factory.gui.ProductionGUI;
import org.RisingSMP.factory.production.CompleteProductionSystem;
import org.RisingSMP.factory.production.CompleteProductionSystem.ProductionRecipe;

public class ProductionCommand implements CommandExecutor {
    
    private final ProductionGUI productionGUI;
    
    public ProductionCommand() {
        this.productionGUI = new ProductionGUI();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cQuesto comando può essere usato solo dai giocatori!");
            return true;
        }
        
        if (!player.hasPermission("factory.production")) {
            player.sendMessage("§cNon hai i permessi per usare questo comando!");
            return true;
        }
        
        if (args.length == 0) {
            // Apre la GUI di produzione
            productionGUI.openMainMenu(player);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "list":
                showProductionList(player);
                break;
                
            case "craft":
                if (args.length < 2) {
                    player.sendMessage("§cUso: /factory craft <item>");
                    return true;
                }
                craftItem(player, args[1]);
                break;
                
            case "info":
                if (args.length < 2) {
                    player.sendMessage("§cUso: /factory info <item>");
                    return true;
                }
                showItemInfo(player, args[1]);
                break;
                
            case "categories":
                showCategories(player);
                break;
                
            default:
                showHelp(player);
                break;
        }
        
        return true;
    }
    
    private void showProductionList(Player player) {
        player.sendMessage("§6=== Lista Produzione ===");
        
        for (String categoryId : CompleteProductionSystem.getAllCategories().keySet()) {
            var category = CompleteProductionSystem.getCategory(categoryId);
            var recipes = CompleteProductionSystem.getRecipesByCategory(categoryId);
            
            player.sendMessage("§e" + category.getDisplayName() + " §7(" + recipes.size() + " item)");
        }
        
        player.sendMessage("§7Usa §e/factory <categoria> §7per vedere gli item specifici");
    }
    
    private void craftItem(Player player, String itemName) {
        ProductionRecipe recipe = CompleteProductionSystem.getRecipe(itemName.toLowerCase());
        
        if (recipe == null) {
            player.sendMessage("§cItem non trovato! Usa §e/factory list §7per vedere gli item disponibili.");
            return;
        }
        
        if (CompleteProductionSystem.canCraft(player, recipe.getId())) {
            if (CompleteProductionSystem.craftItem(player, recipe.getId())) {
                player.sendMessage("§a✓ Item prodotto con successo!");
            } else {
                player.sendMessage("§c✗ Errore durante la produzione!");
            }
        } else {
            player.sendMessage("§c✗ Non hai i materiali necessari!");
            player.sendMessage("§7Materiali richiesti:");
            
            for (String ingredient : recipe.getIngredients()) {
                String[] parts = ingredient.split(":");
                String material = parts[0].toLowerCase().replace("_", " ");
                int amount = Integer.parseInt(parts[1]);
                player.sendMessage("§7- §f" + amount + "x " + material);
            }
        }
    }
    
    private void showItemInfo(Player player, String itemName) {
        ProductionRecipe recipe = CompleteProductionSystem.getRecipe(itemName.toLowerCase());
        
        if (recipe == null) {
            player.sendMessage("§cItem non trovato!");
            return;
        }
        
        player.sendMessage("§6=== Informazioni Item ===");
        player.sendMessage("§eItem: §f" + recipe.getResult().getType().name());
        player.sendMessage("§eCosto Energia: §f" + recipe.getEnergyCost());
        player.sendMessage("§eCategoria: §f" + CompleteProductionSystem.getCategory(recipe.getCategory()).getDisplayName());
        
        player.sendMessage("§6Materiali Richiesti:");
        for (String ingredient : recipe.getIngredients()) {
            String[] parts = ingredient.split(":");
            String material = parts[0].toLowerCase().replace("_", " ");
            int amount = Integer.parseInt(parts[1]);
            player.sendMessage("§7- §f" + amount + "x " + material);
        }
    }
    
    private void showCategories(Player player) {
        player.sendMessage("§6=== Categorie Produzione ===");
        
        for (String categoryId : CompleteProductionSystem.getAllCategories().keySet()) {
            var category = CompleteProductionSystem.getCategory(categoryId);
            var recipes = CompleteProductionSystem.getRecipesByCategory(categoryId);
            
            player.sendMessage("§e" + category.getDisplayName() + " §7(" + recipes.size() + " item)");
            player.sendMessage("  §7- §f/factory " + categoryId + " §7per vedere gli item");
        }
    }
    
    private void showHelp(Player player) {
        player.sendMessage("§6=== Comandi Produzione ===");
        player.sendMessage("§e/factory §f- Apre la GUI di produzione");
        player.sendMessage("§e/factory list §f- Mostra tutte le categorie");
        player.sendMessage("§e/factory categories §f- Mostra categorie disponibili");
        player.sendMessage("§e/factory craft <item> §f- Produce un item");
        player.sendMessage("§e/factory info <item> §f- Mostra info item");
        player.sendMessage("");
        player.sendMessage("§7Esempi:");
        player.sendMessage("§e/factory craft iron_pickaxe");
        player.sendMessage("§e/factory info diamond_sword");
    }
}

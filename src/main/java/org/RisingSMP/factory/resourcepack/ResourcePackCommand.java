package org.RisingSMP.factory.resourcepack;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.RisingSMP.factory.Factory;

public class ResourcePackCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("factory.admin")) {
            player.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }
        
        if (args.length == 0) {
            showHelp(player);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "status":
                player.sendMessage(ResourcePackManager.getResourcePackStatus());
                break;
                
            case "send":
                ResourcePackManager.forceSendResourcePack(player);
                player.sendMessage("§aResource pack sent!");
                break;
                
            case "reload":
                ResourcePackManager.reloadResourcePack();
                player.sendMessage("§aResource pack reloaded and sent to all players!");
                break;
                
            case "generate":
                player.sendMessage("§eGenerating new resource pack...");
                ResourcePackManager.reloadResourcePack();
                break;
                
            default:
                showHelp(player);
                break;
        }
        
        return true;
    }
    
    private void showHelp(Player player) {
        player.sendMessage("§6=== Resource Pack Commands ===");
        player.sendMessage("§7/factoryrp status §f- Show resource pack status");
        player.sendMessage("§7/factoryrp send §f- Send resource pack to you");
        player.sendMessage("§7/factoryrp reload §f- Reload and send to all players");
        player.sendMessage("§7/factoryrp generate §f- Generate new resource pack");
    }
}

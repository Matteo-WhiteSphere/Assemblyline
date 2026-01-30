package org.RisingSMP.factory.listeners;

import org.RisingSMP.factory.machines.GeneratorMachine;
import org.RisingSMP.factory.machines.RefineryMachine;
import org.RisingSMP.factory.machines.FinalMachine;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class FactoryUpgradeListener implements Listener {
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Generator Control") || 
            event.getView().getTitle().equals("Refinery Control")) {
            
            event.setCancelled(true);
            
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || clicked.getType() == Material.AIR) return;
            
            Player player = (Player) event.getWhoClicked();
            
            // Solo click su EMERALD_BLOCK per upgrade
            if (clicked.getType() == Material.EMERALD_BLOCK && 
                (clicked.getItemMeta().getDisplayName().contains("Upgrade"))) {
                
                handleUpgrade(player, event.getView().getTitle());
            }
        }
    }
    
    private void handleUpgrade(Player player, String guiTitle) {
        // Qui implementeremo la logica di upgrade
        // Per ora solo messaggio di conferma
        player.sendMessage("§a[Factory] Upgrade richiesto per: " + guiTitle);
        player.sendMessage("§7Funzionalità in sviluppo...");
    }
}

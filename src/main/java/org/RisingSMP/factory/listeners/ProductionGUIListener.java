package org.RisingSMP.factory.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import org.RisingSMP.factory.gui.ProductionGUI;

public class ProductionGUIListener implements Listener {
    
    private final ProductionGUI productionGUI;
    
    public ProductionGUIListener() {
        this.productionGUI = new ProductionGUI();
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        
        String title = event.getView().getTitle();
        
        // Controlla se è una GUI di produzione
        if (title.equals("§6Factory Production System") || title.startsWith("§6")) {
            event.setCancelled(true);
            
            if (event.getCurrentItem() == null) return;
            
            // Gestisci il click
            productionGUI.handleClick(player, event.getCurrentItem(), event.getSlot());
        }
    }
    
    public ProductionGUI getProductionGUI() {
        return productionGUI;
    }
}

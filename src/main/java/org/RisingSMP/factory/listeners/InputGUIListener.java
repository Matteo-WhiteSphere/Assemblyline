package org.RisingSMP.factory.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import org.RisingSMP.factory.gui.InputGUI;

public class InputGUIListener implements Listener {
    
    private final InputGUI inputGUI;
    
    public InputGUIListener() {
        this.inputGUI = new InputGUI();
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        
        String title = event.getView().getTitle();
        
        // Controlla se è la GUI Input
        if (title.equals("§6Input Factory Control")) {
            event.setCancelled(true);
            
            if (event.getCurrentItem() == null) return;
            
            // Gestisci il click
            inputGUI.handleClick(player, event.getCurrentItem(), event.getSlot());
        }
    }
    
    public InputGUI getInputGUI() {
        return inputGUI;
    }
}

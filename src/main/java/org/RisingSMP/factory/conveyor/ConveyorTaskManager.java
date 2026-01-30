package org.RisingSMP.factory.conveyor;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.entity.Item;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class ConveyorTaskManager {
    
    private static final List<Item> transportedItems = new ArrayList<>();
    
    public static void startConveyorSystem() {
        new BukkitRunnable() {
            @Override
            public void run() {
                processConveyors();
            }
        }.runTaskTimer(org.RisingSMP.factory.Factory.getInstance(), 5L, 5L); // Ogni 0.25 secondi
    }
    
    private static void processConveyors() {
        // Processa tutti gli item trasportati
        for (Item item : new ArrayList<>(transportedItems)) {
            if (item.isDead() || !item.isValid()) {
                transportedItems.remove(item);
                continue;
            }
            
            Location loc = item.getLocation();
            Block block = loc.getBlock();
            
            // Controlla se è su un conveyor
            ConveyorSystem.ConveyorType type = ConveyorSystem.getType(block);
            if (type != null) {
                // Trasporta l'item
                ConveyorSystem.transportItem(item, block);
                
                // Controlla destinazione
                Location nextLoc = ConveyorSystem.getNextLocation(block);
                if (nextLoc != null) {
                    Block nextBlock = nextLoc.getBlock();
                    
                    // Inserisci in chest se presente
                    if (nextBlock.getState() instanceof Chest chest) {
                        insertIntoChest(item, chest);
                    }
                }
            } else {
                // Rimuovi se non più su conveyor
                transportedItems.remove(item);
            }
        }
    }
    
    public static void addItemToTransport(Item item) {
        if (!transportedItems.contains(item)) {
            transportedItems.add(item);
        }
    }
    
    private static void insertIntoChest(Item item, Chest chest) {
        Inventory inv = chest.getBlockInventory();
        org.bukkit.inventory.ItemStack itemStack = item.getItemStack();
        
        if (inv.firstEmpty() != -1) {
            inv.addItem(itemStack);
            item.remove();
            transportedItems.remove(item);
        }
    }
    
    public static List<Item> getTransportedItems() {
        return new ArrayList<>(transportedItems);
    }
}

package org.RisingSMP.factory.machines;

import org.RisingSMP.factory.energy.EnergyManager;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class OutputMachine extends Machine {

    public OutputMachine(Block block) {
        super(block);
    }

    @Override
    public void process(Item item) {
        // OUTPUT prende item sopra e li mette nella chest più vicina
        if (item == null || item.isDead()) return;

        // ✅ consumo energia reale
        if (!EnergyManager.consumeEnergy(block, getEnergyCost())) return;

        // Cerca la chest più vicina entro 1 blocco di distanza
        Chest nearestChest = findNearestChest();
        if (nearestChest == null) return;

        Inventory chestInv = nearestChest.getBlockInventory();
        ItemStack itemStack = item.getItemStack();

        // Prova a mettere l'item nella chest
        ItemStack remaining = chestInv.addItem(itemStack).get(0);
        
        if (remaining == null || remaining.getAmount() == 0) {
            // Item completamente inserito nella chest
            item.remove();
        } else {
            // Solo parte dell'item inserita
            item.setItemStack(remaining);
        }
    }

    @Override
    public int getEnergyCost() {
        return 1; // costo per trasferire 1 item
    }

    /**
     * Trova la chest più vicina entro 1 blocco di distanza in tutte le direzioni
     */
    private Chest findNearestChest() {
        Chest nearestChest = null;
        double minDistance = Double.MAX_VALUE;

        // Controlla tutti i blocchi in un cubo 3x3x3 centrato sulla macchina
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && y == 0 && z == 0) continue; // Salta il blocco della macchina

                    Block checkBlock = block.getRelative(x, y, z);
                    
                    if (checkBlock.getState() instanceof Chest chest) {
                        double distance = Math.sqrt(x*x + y*y + z*z);
                        if (distance < minDistance) {
                            minDistance = distance;
                            nearestChest = chest;
                        }
                    }
                }
            }
        }

        return nearestChest;
    }
}

package org.RisingSMP.factory.machines;

import org.RisingSMP.factory.energy.EnergyManager;
import org.RisingSMP.factory.items.FactoryItems;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class RefineryMachine extends Machine {

    public RefineryMachine(Block block) {
        super(block);
    }

    @Override
    public void process(Item item) {
        // Metodo vuoto, processing diretto nel tick()
    }

    @Override
    public int getEnergyCost() {
        return Math.max(1, 5 - (getLevel() - 1)); // Riduce consumo con livello
    }

    public int getLevel() {
        if (!(block.getState() instanceof TileState state)) return 1;
        return state.getPersistentDataContainer()
                .getOrDefault(FactoryItems.LEVEL_KEY, PersistentDataType.INTEGER, 1);
    }
    
    public void upgrade() {
        if (!(block.getState() instanceof TileState state)) return;

        int level = getLevel();
        state.getPersistentDataContainer()
                .set(FactoryItems.LEVEL_KEY, PersistentDataType.INTEGER, level + 1);

        state.update();
    }
    
    public int getUpgradeCost(int currentLevel) {
        return switch (currentLevel) {
            case 1 -> 8;   // 8 emerald blocks
            case 2 -> 20;  // 20 emerald blocks  
            case 3 -> 40;  // 40 emerald blocks
            case 4 -> 80;  // 80 emerald blocks
            default -> 150; // 150+ per livelli alti
        };
    }
    
    public boolean canUpgrade() {
        return getLevel() < 10; // Max livello 10
    }
    
    public int getProcessingSpeed() {
        return getLevel(); // Velocità di processing = livello
    }

    public void tick() {
        // Controllo energia
        if (!EnergyManager.consumeEnergy(block, getEnergyCost())) return;

        // Cerca inventario collegato (es. chest sopra)
        Block above = block.getRelative(org.bukkit.block.BlockFace.UP);
        Inventory inv = null;
        if (above.getState() instanceof org.bukkit.block.Chest) {
            inv = ((org.bukkit.block.Chest) above.getState()).getBlockInventory();
        }
        if (inv == null) return;

        // Cerca carbone
        ItemStack coal = null;
        int slot = -1;

        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (stack == null) continue;
            if (stack.getType() == Material.COAL_BLOCK) {
                coal = stack;
                slot = i;
                break;
            }
        }

        if (coal == null) return;

        // Consuma 1 carbone
        coal.setAmount(coal.getAmount() - 1);
        if (coal.getAmount() <= 0) {
            inv.setItem(slot, null);
        }

        // Produce petrolio
        ItemStack oil = FactoryItems.OIL_BLOCK.clone();

        // Output: chest davanti alla raffineria (es. BlockFace.NORTH)
        Block outputBlock = block.getRelative(org.bukkit.block.BlockFace.NORTH);
        Inventory outputInv = null;
        if (outputBlock.getState() instanceof org.bukkit.block.Chest) {
            outputInv = ((org.bukkit.block.Chest) outputBlock.getState()).getBlockInventory();
        }
        if (outputInv != null) {
            outputInv.addItem(oil);
        }
    }
}
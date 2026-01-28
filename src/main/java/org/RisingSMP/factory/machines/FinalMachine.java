package org.RisingSMP.factory.machines;

import org.RisingSMP.factory.energy.EnergyManager;
import org.RisingSMP.factory.Factory;
import org.RisingSMP.factory.gui.FactoryGUI;
import org.RisingSMP.factory.items.FactoryItems;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.TileState;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class FinalMachine extends Machine {

    public FinalMachine(Block block) {
        super(block);
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
            case 1 -> 15;  // 15 emerald blocks
            case 2 -> 35;  // 35 emerald blocks  
            case 3 -> 70;  // 70 emerald blocks
            case 4 -> 120; // 120 emerald blocks
            default -> 250; // 250+ per livelli alti
        };
    }
    
    public boolean canUpgrade() {
        return getLevel() < 10; // Max livello 10
    }
    
    public int getOutputMultiplier() {
        return 1 + (getLevel() - 1); // +1 output per livello
    }

    private ItemStack getFinalItem(String typeName) {
        // Prende l'item dalla mappa FactoryGUI.factoryWeapons
        if (FactoryGUI.factoryWeapons.containsKey(typeName)) {
            ItemStack original = FactoryGUI.factoryWeapons.get(typeName);
            if (original != null) {
                return new ItemStack(original.getType(), original.getAmount());
            }
        }
        // default se non presente
        return new ItemStack(Material.DIAMOND_SWORD);
    }

    @Override
    public void process(Item item) {
        ItemStack input = item.getItemStack();

        // Consumo energia reale
        if (!EnergyManager.consumeEnergy(block, getEnergyCost())) return;

        // Controlla input
        if (input.getType() != Material.IRON_INGOT) return;

        if (!(block.getState() instanceof TileState state)) return;

        // Direzione verso chest output
        String facingStr = state.getPersistentDataContainer()
                .getOrDefault(
                        new NamespacedKey(Factory.instance, "facing"),
                        PersistentDataType.STRING,
                        "NORTH"
                );
        BlockFace facing = BlockFace.valueOf(facingStr);

        Block outBlock = block.getRelative(facing);
        if (!(outBlock.getState() instanceof Chest chest)) return;

        Inventory outInv = chest.getBlockInventory();
        if (outInv.firstEmpty() == -1) return;

        // Recupera tipo di arma o proiettile da configurazione
        // Ad esempio "Musket" o "Bullet"
        String outputType = FactoryGUI.factoryWeaponsType.getOrDefault("FINAL", "Musket");

        ItemStack finalItem = getFinalItem(outputType);

        // Aggiungi direttamente nella chest
        outInv.addItem(finalItem);

        // Consuma input
        item.remove();
    }

    @Override
    public int getEnergyCost() {
        return 3;
    }
}
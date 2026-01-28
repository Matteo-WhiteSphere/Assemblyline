package org.RisingSMP.factory.machines;

import org.RisingSMP.factory.energy.EnergyManager;
import org.RisingSMP.factory.items.FactoryItems;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.TileState;
import org.bukkit.entity.Item;
import org.bukkit.persistence.PersistentDataType;

public class GeneratorMachine extends Machine {

    public enum GeneratorType {
        OIL,       // petrolio
        COAL,      // carbone
        SOLAR,     // solare
        WIND,      // eolico
        NUCLEAR    // nucleare
    }

    private final GeneratorType type;
    private boolean active = false;

    public static final int RANGE = 64;

    public GeneratorMachine(Block block, GeneratorType type) {
        super(block);
        this.type = type;
    }
    
    // Costruttore di default per compatibilità
    public GeneratorMachine(Block block) {
        this(block, GeneratorType.COAL);
    }

    @Override
    public void process(Item item) {
        // Il generatore NON processa item lanciati
    }

    /* =========================
       [OLD/SAVE] TICK
       =========================
    public void tick() {

        switch (type) {

            case OIL -> {
                active = consumeFuel(Material.COAL_BLOCK);
            }

            case COAL -> {
                active = consumeFuel(Material.COAL);
            }

            case SOLAR -> {
                long time = block.getWorld().getTime();
                boolean day = time > 0 && time < 12000;
                boolean sky = block.getRelative(BlockFace.UP).getType() == Material.AIR;
                active = day && sky;
            }

            case WIND -> {
                boolean sky = block.getRelative(BlockFace.UP).getType() == Material.AIR;
                active = sky;
            }
        }
    }
    */

        /* =========================
       TICK (consumo / stato) aggiornato
       =========================*/
    public void tick() {
        switch (type) {
            case OIL -> active = consumeFuel(Material.COAL_BLOCK);
            case COAL -> active = consumeFuel(Material.COAL);
            case SOLAR -> {
                long time = block.getWorld().getTime();
                boolean day = time > 0 && time < 12000;
                boolean sky = block.getRelative(BlockFace.UP).getType() == Material.AIR;
                active = day && sky;
            }
            case WIND -> {
                boolean sky = block.getRelative(BlockFace.UP).getType() == Material.AIR;
                active = sky;
            }
            case NUCLEAR -> active = consumeFuel(Material.EMERALD_BLOCK); // es. Uranio
        }
    }


    private boolean consumeFuel(Material fuel) {
        Block above = block.getRelative(BlockFace.UP);
        if (above.getType() != fuel) return false;

        int level = getLevel();

        // ogni livello consuma 1 blocco
        above.setType(Material.AIR);

        return true;
    }

    public boolean isActive() {
        Block above = block.getRelative(BlockFace.UP);
        return above.getType() == Material.COAL_BLOCK; // petrolio
    }


    public int getLevel() {
        if (!(block.getState() instanceof TileState state)) return 1;
        return state.getPersistentDataContainer()
                .getOrDefault(FactoryItems.LEVEL_KEY, PersistentDataType.INTEGER, 1);
    }

    public int getRange() {
        return switch (type) {
            case OIL -> 64 + (getLevel() - 1) * 16;
            case COAL -> 48 + (getLevel() - 1) * 12;
            case SOLAR -> 32 + (getLevel() - 1) * 8;
            case WIND -> 40 + (getLevel() - 1) * 10;
            case NUCLEAR -> 80 + (getLevel() - 1) * 20;
        };
    }

    public void upgrade() {
        if (!(block.getState() instanceof TileState state)) return;

        int level = getLevel();
        int cost = getUpgradeCost(level);
        
        state.getPersistentDataContainer()
                .set(FactoryItems.LEVEL_KEY, PersistentDataType.INTEGER, level + 1);

        state.update();
    }
    
    public int getUpgradeCost(int currentLevel) {
        return switch (currentLevel) {
            case 1 -> 10;  // 10 emerald blocks
            case 2 -> 25;  // 25 emerald blocks  
            case 3 -> 50;  // 50 emerald blocks
            case 4 -> 100; // 100 emerald blocks
            default -> 200; // 200+ per livelli alti
        };
    }
    
    public boolean canUpgrade() {
        return getLevel() < 10; // Max livello 10
    }

    public int getProduction() {
        return 5 * getLevel(); // base, puoi personalizzare per tipo
    }
}

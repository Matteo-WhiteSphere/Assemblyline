package org.RisingSMP.factory.machines;

import org.RisingSMP.factory.energy.EnergyManager;
import org.RisingSMP.factory.items.FactoryItems;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;

public class GeneratorMachine extends Machine {

    public enum GeneratorType {
        OIL,
        COAL,
        SOLAR,
        WIND
    }

    private final GeneratorType type;
    private boolean active = false;

    public static final int RANGE = 64;

    public GeneratorMachine(Block block, GeneratorType type) {
        super(block);
        this.type = type;
    }

    @Override
    public void process(Item item) {
        // Il generatore NON processa item lanciati
    }

    /* =========================
       TICK (consumo / stato)
       ========================= */
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

    private boolean consumeFuel(Material fuel) {
        Block above = block.getRelative(BlockFace.UP);
        if (above.getType() == fuel) {
            above.setType(Material.AIR);
            return true;
        }
        return false;
    }

    public boolean isActive() {
        return active;
    }

    public int getRange() {
        return RANGE;
    }
}
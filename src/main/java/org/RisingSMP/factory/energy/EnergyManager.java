package org.RisingSMP.factory.energy;

import org.RisingSMP.factory.items.FactoryItems;
import org.RisingSMP.factory.machines.GeneratorMachine;
import org.RisingSMP.factory.machines.GeneratorMachine.GeneratorType;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.block.TileState;

public class EnergyManager {

    public static boolean consumeEnergy(Block machineBlock, int cost) {

        int available = 0;

        for (int dx = -80; dx <= 80; dx++) {
            for (int dy = -20; dy <= 20; dy++) {
                for (int dz = -80; dz <= 80; dz++) {

                    Block b = machineBlock.getRelative(dx, dy, dz);

                    GeneratorType type = getGeneratorType(b);
                    if (type == null) continue;

                    GeneratorMachine gen = new GeneratorMachine(b, type);

                    if (!gen.isActive()) continue;

                    int range = gen.getRange();

                    if (Math.abs(dx) > range || Math.abs(dz) > range) continue;

                    available += gen.getProduction();

                    if (available >= cost) {
                        return true; // energia sufficiente
                    }
                }
            }
        }
        return false;
    }

    private static GeneratorType getGeneratorType(Block block) {

        if (!(block.getState() instanceof TileState state)) return null;

        String type = state.getPersistentDataContainer()
                .get(FactoryItems.FACTORY_KEY, PersistentDataType.STRING);

        if (type == null) return null;

        return switch (type) {
            case "GENERATOR_OIL" -> GeneratorType.OIL;
            case "GENERATOR_COAL" -> GeneratorType.COAL;
            case "GENERATOR_SOLAR" -> GeneratorType.SOLAR;
            case "GENERATOR_WIND" -> GeneratorType.WIND;
            default -> null;
        };
    }
}
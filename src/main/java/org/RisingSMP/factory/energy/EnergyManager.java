package org.RisingSMP.factory.energy;

import org.RisingSMP.factory.items.FactoryItems;
import org.RisingSMP.factory.machines.GeneratorMachine;
import org.RisingSMP.factory.machines.GeneratorMachine.GeneratorType;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.block.TileState;

public class EnergyManager {

    public static boolean hasEnergy(Block machineBlock) {

        for (int x = -64; x <= 64; x++) {
            for (int y = -16; y <= 16; y++) {
                for (int z = -64; z <= 64; z++) {

                    Block b = machineBlock.getRelative(x, y, z);

                    GeneratorType type = getGeneratorType(b);
                    if (type == null) continue;

                    GeneratorMachine gen = new GeneratorMachine(b, type);
                    gen.tick();

                    if (gen.isActive()) return true;
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
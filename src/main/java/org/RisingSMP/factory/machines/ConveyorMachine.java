package org.RisingSMP.factory.machines;

import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.util.Vector;
import org.bukkit.block.TileState;
import org.bukkit.persistence.PersistentDataType;
import org.RisingSMP.factory.Factory;
import org.RisingSMP.factory.FactoryItems;
import org.RisingSMP.factory.energy.EnergyManager;

public class ConveyorMachine extends Machine {

    public ConveyorMachine(Block block) {
        super(block);
    }

    @Override
    public void process(Item item) {
        if (!EnergyManager.hasEnergy(block)) return;
        if (!(block.getState() instanceof TileState state)) return;

        String facing = state.getPersistentDataContainer()
                .get(new org.bukkit.NamespacedKey(Factory.instance, "facing"), PersistentDataType.STRING);
        Vector dir = switch (facing != null ? facing : "NORTH") {
            case "NORTH" -> new Vector(0, 0, -0.2);
            case "SOUTH" -> new Vector(0, 0, 0.2);
            case "WEST" -> new Vector(-0.2, 0, 0);
            case "EAST" -> new Vector(0.2, 0, 0);
            default -> new Vector(0, 0, 0.2);
        };

        item.setVelocity(dir);
    }
}
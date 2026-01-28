package org.RisingSMP.factory.machines;

import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.RisingSMP.factory.energy.EnergyManager;

public abstract class Machine {

    protected Block block;

    public Machine(Block block) {
        this.block = block;
    }

    /**
     * Logica che viene eseguita ogni tick sugli item sopra il blocco
     */
    public abstract void process(Item item);

    public int getEnergyCost() {
        return 1; // default
    }
    
    public Block getBlock() {
        return block;
    }
}
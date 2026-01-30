package org.RisingSMP.factory.energy;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;

public class EnergyBuffer {
    
    private static final NamespacedKey BUFFER_KEY = new NamespacedKey("factory", "energy_buffer");
    private static final NamespacedKey CAPACITY_KEY = new NamespacedKey("factory", "energy_capacity");
    
    private final Block block;
    
    public EnergyBuffer(Block block) {
        this.block = block;
    }
    
    public int getEnergy() {
        if (!(block.getState() instanceof TileState state)) return 0;
        return state.getPersistentDataContainer()
                .getOrDefault(BUFFER_KEY, PersistentDataType.INTEGER, 0);
    }
    
    public void setEnergy(int amount) {
        if (!(block.getState() instanceof TileState state)) return;
        state.getPersistentDataContainer()
                .set(BUFFER_KEY, PersistentDataType.INTEGER, Math.max(0, amount));
        state.update();
    }
    
    public int getCapacity() {
        if (!(block.getState() instanceof TileState state)) return 1000;
        return state.getPersistentDataContainer()
                .getOrDefault(CAPACITY_KEY, PersistentDataType.INTEGER, 1000);
    }
    
    public void setCapacity(int capacity) {
        if (!(block.getState() instanceof TileState state)) return;
        state.getPersistentDataContainer()
                .set(CAPACITY_KEY, PersistentDataType.INTEGER, capacity);
        state.update();
    }
    
    public int addEnergy(int amount) {
        int current = getEnergy();
        int capacity = getCapacity();
        int canAdd = Math.min(amount, capacity - current);
        
        if (canAdd > 0) {
            setEnergy(current + canAdd);
        }
        
        return canAdd;
    }
    
    public int consumeEnergy(int amount) {
        int current = getEnergy();
        int canConsume = Math.min(amount, current);
        
        if (canConsume > 0) {
            setEnergy(current - canConsume);
        }
        
        return canConsume;
    }
    
    public boolean hasEnergy(int amount) {
        return getEnergy() >= amount;
    }
    
    public double getPercentage() {
        return (double) getEnergy() / getCapacity() * 100.0;
    }
}

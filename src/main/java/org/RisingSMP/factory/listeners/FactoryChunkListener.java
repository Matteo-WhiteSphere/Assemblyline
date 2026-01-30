package org.RisingSMP.factory.listeners;

import org.RisingSMP.factory.machines.GeneratorMachine;
import org.RisingSMP.factory.machines.RefineryMachine;
import org.RisingSMP.factory.machines.IntermediateMachine;
import org.RisingSMP.factory.machines.FinalMachine;
import org.RisingSMP.factory.machines.Machine;
import org.RisingSMP.factory.registry.MachineRegistry;
import org.RisingSMP.factory.items.FactoryItems;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.persistence.PersistentDataType;

public class FactoryChunkListener implements Listener {
    
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        // Itera tutti i blocchi nel chunk caricato
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = event.getWorld().getMinHeight(); y < event.getWorld().getMaxHeight(); y++) {
                    Block block = event.getChunk().getBlock(x, y, z);
                    
                    // Controlla solo blocchi con TileState
                    if (!(block.getState() instanceof TileState state)) continue;
                    
                    // Verifica se è una macchina factory
                    String type = state.getPersistentDataContainer()
                            .get(FactoryItems.FACTORY_KEY, PersistentDataType.STRING);
                    
                    if (type != null) {
                        // Ricostruisci la macchina e registrala
                        rebuildMachine(block, type);
                    }
                }
            }
        }
    }
    
    private void rebuildMachine(Block block, String type) {
        Machine machine = null;
        
        switch (type) {
            case "GENERATOR" -> machine = new GeneratorMachine(block);
            case "REFINERY" -> machine = new RefineryMachine(block);
            case "INTERMEDIATE" -> machine = new IntermediateMachine(block);
            case "FINAL" -> machine = new FinalMachine(block);
        }
        
        if (machine != null) {
            MachineRegistry.register(block, machine);
        }
    }
}

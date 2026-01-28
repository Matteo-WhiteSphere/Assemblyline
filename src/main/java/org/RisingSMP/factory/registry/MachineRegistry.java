package org.RisingSMP.factory.registry;

import org.bukkit.block.Block;
import org.RisingSMP.factory.machines.Machine;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MachineRegistry {

    private static final Map<Block, Machine> machines = new HashMap<>();

    // registra una macchina
    public static void register(Block block, Machine machine) {
        machines.put(block, machine);
    }

    // rimuove una macchina
    public static void unregister(Block block) {
        machines.remove(block);
    }

    // prende la macchina da un blocco
    public static Machine get(Block block) {
        return machines.get(block);
    }

    // tutte le macchine
    public static Collection<Machine> getAll() {
        return machines.values();
    }
    
    // alias per compatibilità
    public static Collection<Machine> getAllMachines() {
        return getAll();
    }
}
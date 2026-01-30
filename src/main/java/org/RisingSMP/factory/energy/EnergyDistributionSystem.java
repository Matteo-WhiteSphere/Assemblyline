package org.RisingSMP.factory.energy;

import org.RisingSMP.factory.registry.MachineRegistry;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class EnergyDistributionSystem {
    
    private static final Map<Material, Integer> ENERGY_PRIORITY = new HashMap<>();
    
    static {
        ENERGY_PRIORITY.put(Material.COAL_BLOCK, 1);      // Generatori
        ENERGY_PRIORITY.put(Material.IRON_BLOCK, 2);     // Raffinerie  
        ENERGY_PRIORITY.put(Material.GOLD_BLOCK, 3);      // Intermedie
        ENERGY_PRIORITY.put(Material.DIAMOND_BLOCK, 4);  // Final
    }
    
    public static void startDistribution() {
        new BukkitRunnable() {
            @Override
            public void run() {
                distributeEnergy();
            }
        }.runTaskTimer(org.RisingSMP.factory.Factory.getInstance(), 20L, 20L); // Ogni secondo
    }
    
    private static void distributeEnergy() {
        // Calcola energia totale prodotta
        int totalProduced = MachineRegistry.getAllMachines().stream()
                .filter(m -> m instanceof org.RisingSMP.factory.machines.GeneratorMachine)
                .mapToInt(m -> ((org.RisingSMP.factory.machines.GeneratorMachine) m).getProduction())
                .sum();
        
        // Distribuisci alle macchine per priorità
        int remaining = totalProduced;
        
        for (Map.Entry<Material, Integer> priority : ENERGY_PRIORITY.entrySet()) {
            if (remaining <= 0) break;
            
            remaining = distributeToType(priority.getKey(), priority.getValue(), remaining);
        }
    }
    
    private static int distributeToType(Material materialType, int priority, int availableEnergy) {
        int distributed = 0;
        
        for (org.RisingSMP.factory.machines.Machine machine : MachineRegistry.getAllMachines()) {
            if (distributed >= availableEnergy) break;
            
            if (machine.getBlock().getType() == materialType) {
                int needed = machine.getEnergyCost();
                if (needed > 0 && availableEnergy - distributed >= needed) {
                    // Simula consumo energetico
                    distributed += needed;
                }
            }
        }
        
        return distributed;
    }
    
    public static int getTotalProduction() {
        return MachineRegistry.getAllMachines().stream()
                .filter(m -> m instanceof org.RisingSMP.factory.machines.GeneratorMachine)
                .mapToInt(m -> ((org.RisingSMP.factory.machines.GeneratorMachine) m).getProduction())
                .sum();
    }
    
    public static int getTotalConsumption() {
        return MachineRegistry.getAllMachines().stream()
                .mapToInt(org.RisingSMP.factory.machines.Machine::getEnergyCost)
                .sum();
    }
}

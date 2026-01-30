package org.RisingSMP.factory.io;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.TileState;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;

import java.util.HashMap;
import java.util.Map;

public class IOManager {
    
    private static final NamespacedKey INPUT_KEY = new NamespacedKey("factory", "input_config");
    private static final NamespacedKey OUTPUT_KEY = new NamespacedKey("factory", "output_config");
    
    public enum IOType {
        INPUT(Material.HOPPER),
        OUTPUT(Material.DROPPER);
        
        private final Material material;
        
        IOType(Material material) {
            this.material = material;
        }
        
        public Material getMaterial() { return material; }
    }
    
    public static void configureIO(Block machine, IOType type, Block chest) {
        if (!(machine.getState() instanceof TileState state)) return;
        
        Map<String, String> config = getIOConfig(machine, type);
        config.put(chest.getX() + "," + chest.getY() + "," + chest.getZ(), chest.getType().name());
        
        saveIOConfig(machine, type, config);
    }
    
    private static Map<String, String> getIOConfig(Block machine, IOType type) {
        if (!(machine.getState() instanceof TileState state)) return new HashMap<>();
        
        String configData = state.getPersistentDataContainer()
                .get(type == IOType.INPUT ? INPUT_KEY : OUTPUT_KEY, PersistentDataType.STRING);
        
        Map<String, String> config = new HashMap<>();
        if (configData != null && !configData.isEmpty()) {
            String[] pairs = configData.split(";");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    config.put(keyValue[0], keyValue[1]);
                }
            }
        }
        
        return config;
    }
    
    private static void saveIOConfig(Block machine, IOType type, Map<String, String> config) {
        if (!(machine.getState() instanceof TileState state)) return;
        
        StringBuilder configData = new StringBuilder();
        for (Map.Entry<String, String> entry : config.entrySet()) {
            if (configData.length() > 0) configData.append(";");
            configData.append(entry.getKey()).append("=").append(entry.getValue());
        }
        
        state.getPersistentDataContainer()
                .set(type == IOType.INPUT ? INPUT_KEY : OUTPUT_KEY, 
                      PersistentDataType.STRING, configData.toString());
        state.update();
    }
    
    public static Inventory getInputInventory(Block machine) {
        Map<String, String> inputConfig = getIOConfig(machine, IOType.INPUT);
        
        for (String location : inputConfig.keySet()) {
            String[] coords = location.split(",");
            Block chest = machine.getWorld().getBlockAt(
                    Integer.parseInt(coords[0]),
                    Integer.parseInt(coords[1]), 
                    Integer.parseInt(coords[2])
            );
            
            if (chest.getState() instanceof Chest) {
                return ((Chest) chest.getState()).getBlockInventory();
            }
        }
        
        return null;
    }
    
    public static Inventory getOutputInventory(Block machine) {
        Map<String, String> outputConfig = getIOConfig(machine, IOType.OUTPUT);
        
        // Prima chest vuota disponibile
        for (String location : outputConfig.keySet()) {
            String[] coords = location.split(",");
            Block chest = machine.getWorld().getBlockAt(
                    Integer.parseInt(coords[0]),
                    Integer.parseInt(coords[1]), 
                    Integer.parseInt(coords[2])
            );
            
            if (chest.getState() instanceof Chest chestState) {
                Inventory inv = chestState.getBlockInventory();
                if (inv.firstEmpty() != -1) {
                    return inv;
                }
            }
        }
        
        return null;
    }
    
    public static boolean hasInputSpace(Block machine, ItemStack item) {
        Inventory inputInv = getInputInventory(machine);
        if (inputInv == null) return false;
        
        return inputInv.firstEmpty() != -1 || 
               inputInv.containsAtLeast(item, item.getAmount());
    }
    
    public static boolean hasOutputSpace(Block machine, ItemStack item) {
        Inventory outputInv = getOutputInventory(machine);
        if (outputInv == null) return false;
        
        return outputInv.firstEmpty() != -1 || 
               outputInv.containsAtLeast(item, item.getAmount());
    }
    
    public static void addToInput(Block machine, ItemStack item) {
        Inventory inputInv = getInputInventory(machine);
        if (inputInv != null) {
            inputInv.addItem(item);
        }
    }
    
    public static void addToOutput(Block machine, ItemStack item) {
        Inventory outputInv = getOutputInventory(machine);
        if (outputInv != null) {
            outputInv.addItem(item);
        }
    }
}

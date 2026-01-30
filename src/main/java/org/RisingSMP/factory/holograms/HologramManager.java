package org.RisingSMP.factory.holograms;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.RisingSMP.factory.Factory;
import org.RisingSMP.factory.machines.Machine;
import org.RisingSMP.factory.config.FactoryConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HologramManager {
    
    private static boolean hologramsEnabled = false;
    private static Plugin hologramPlugin = null;
    private static final Map<UUID, Map<Location, String>> machineHolograms = new HashMap<>();
    
    public static void initialize() {
        // Check for DecentHolograms
        Plugin decentHolograms = Factory.getInstance().getServer().getPluginManager().getPlugin("DecentHolograms");
        if (decentHolograms != null && decentHolograms.isEnabled()) {
            hologramPlugin = decentHolograms;
            hologramsEnabled = true;
            Factory.getInstance().getLogger().info("DecentHolograms found! Holograms enabled.");
            return;
        }
        
        // Check for HolographicDisplays
        Plugin holographicDisplays = Factory.getInstance().getServer().getPluginManager().getPlugin("HolographicDisplays");
        if (holographicDisplays != null && holographicDisplays.isEnabled()) {
            hologramPlugin = holographicDisplays;
            hologramsEnabled = true;
            Factory.getInstance().getLogger().info("HolographicDisplays found! Holograms enabled.");
            return;
        }
        
        Factory.getInstance().getLogger().info("No hologram plugin found. Using chat-based status.");
        hologramsEnabled = false;
    }
    
    public static void createMachineHologram(Machine machine) {
        if (!hologramsEnabled || !FactoryConfig.isGUIAnimationsEnabled()) {
            return;
        }
        
        Location loc = machine.getBlock().getLocation().add(0.5, 1.5, 0.5);
        String hologramText = createMachineStatusText(machine);
        
        try {
            if (hologramPlugin.getName().equals("DecentHolograms")) {
                createDecentHologram(loc, hologramText);
            } else if (hologramPlugin.getName().equals("HolographicDisplays")) {
                createHolographicDisplay(loc, hologramText);
            }
        } catch (Exception e) {
            Factory.getInstance().getLogger().warning("Failed to create hologram: " + e.getMessage());
        }
    }
    
    public static void updateMachineHologram(Machine machine) {
        removeMachineHologram(machine);
        createMachineHologram(machine);
    }
    
    public static void removeMachineHologram(Machine machine) {
        if (!hologramsEnabled) {
            return;
        }
        
        Location loc = machine.getBlock().getLocation().add(0.5, 1.5, 0.5);
        
        try {
            if (hologramPlugin.getName().equals("DecentHolograms")) {
                removeDecentHologram(loc);
            } else if (hologramPlugin.getName().equals("HolographicDisplays")) {
                removeHolographicDisplay(loc);
            }
        } catch (Exception e) {
            Factory.getInstance().getLogger().warning("Failed to remove hologram: " + e.getMessage());
        }
    }
    
    private static String createMachineStatusText(Machine machine) {
        StringBuilder text = new StringBuilder();
        
        // Machine type and level
        text.append("§6").append(machine.getClass().getSimpleName());
        
        // Production status
        text.append("\n§7▶ ").append("§aOperativo");
        
        return text.toString();
    }
    
    // DecentHolograms methods
    private static void createDecentHologram(Location loc, String text) {
        try {
            Class<?> dhApi = Class.forName("eu.decentsoftware.holograms.api.DHAPI");
            java.lang.reflect.Method createHologram = dhApi.getMethod("createHologram", String.class, Location.class);
            java.lang.reflect.Method addLine = dhApi.getMethod("addHologramLine", String.class, Location.class);
            
            Object hologram = createHologram.invoke(null, "factory_machine_" + loc.hashCode(), loc);
            String[] lines = text.split("\n");
            for (String line : lines) {
                addLine.invoke(null, line, loc);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create DecentHologram", e);
        }
    }
    
    private static void removeDecentHologram(Location loc) {
        try {
            Class<?> dhApi = Class.forName("eu.decentsoftware.holograms.api.DHAPI");
            java.lang.reflect.Method removeHologram = dhApi.getMethod("removeHologram", String.class);
            removeHologram.invoke(null, "factory_machine_" + loc.hashCode());
        } catch (Exception e) {
            throw new RuntimeException("Failed to remove DecentHologram", e);
        }
    }
    
    // HolographicDisplays methods
    private static void createHolographicDisplay(Location loc, String text) {
        try {
            Class<?> hdApi = Class.forName("me.filoghost.holographicdisplays.api.HolographicDisplaysAPI");
            Class<?> hologramClass = Class.forName("me.filoghost.holographicdisplays.api.Hologram");
            
            java.lang.reflect.Method createHologram = hdApi.getMethod("createHologram", Location.class);
            Object hologram = createHologram.invoke(null, loc);
            
            java.lang.reflect.Method appendTextLine = hologramClass.getMethod("appendTextLine", String.class);
            String[] lines = text.split("\n");
            for (String line : lines) {
                appendTextLine.invoke(hologram, line);
            }
            
            java.lang.reflect.Method getVisibilityManager = hologramClass.getMethod("getVisibilityManager");
            Object visibilityManager = getVisibilityManager.invoke(hologram);
            
            Class<?> visibilityClass = Class.forName("me.filoghost.holographicdisplays.api.VisibilityManager");
            java.lang.reflect.Method setVisibleByDefault = visibilityClass.getMethod("setVisibleByDefault", boolean.class);
            setVisibleByDefault.invoke(visibilityManager, true);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to create HolographicDisplay", e);
        }
    }
    
    private static void removeHolographicDisplay(Location loc) {
        try {
            Class<?> hdApi = Class.forName("me.filoghost.holographicdisplays.api.HolographicDisplaysAPI");
            java.lang.reflect.Method getHologram = hdApi.getMethod("getHologram", Location.class);
            
            Object hologram = getHologram.invoke(null, loc);
            if (hologram != null) {
                java.lang.reflect.Method delete = hologram.getClass().getMethod("delete");
                delete.invoke(hologram);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to remove HolographicDisplay", e);
        }
    }
    
    // Fallback chat-based status
    public static void sendMachineStatus(Player player, Machine machine) {
        if (hologramsEnabled) {
            return; // Don't send chat if holograms are available
        }
        
        String status = createMachineStatusText(machine);
        player.sendMessage(status);
    }
    
    // Utility methods
    public static boolean isHologramsEnabled() {
        return hologramsEnabled;
    }
    
    public static String getHologramPluginName() {
        return hologramsEnabled ? hologramPlugin.getName() : "None";
    }
    
    public static void refreshAllHolograms() {
        if (!hologramsEnabled) {
            return;
        }
        
        // This would refresh all machine holograms
        // Implementation depends on how we track all machines
        Factory.getInstance().getLogger().info("Refreshing all machine holograms...");
    }
    
    public static void cleanupAllHolograms() {
        if (!hologramsEnabled) {
            return;
        }
        
        try {
            if (hologramPlugin.getName().equals("DecentHolograms")) {
                Class<?> dhApi = Class.forName("eu.decentsoftware.holograms.api.DHAPI");
                java.lang.reflect.Method getHolograms = dhApi.getMethod("getHolograms");
                java.util.Collection<?> holograms = (java.util.Collection<?>) getHolograms.invoke(null);
                
                for (Object hologram : holograms) {
                    java.lang.reflect.Method getName = hologram.getClass().getMethod("getName");
                    String name = (String) getName.invoke(hologram);
                    if (name.startsWith("factory_machine_")) {
                        java.lang.reflect.Method delete = hologram.getClass().getMethod("delete");
                        delete.invoke(hologram);
                    }
                }
            }
        } catch (Exception e) {
            Factory.getInstance().getLogger().warning("Failed to cleanup holograms: " + e.getMessage());
        }
    }
}

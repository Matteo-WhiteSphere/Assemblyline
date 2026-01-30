package org.RisingSMP.factory.vehicles;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class VehiclesIntegration {
    
    private static Plugin vehiclesPlugin;
    private static boolean isVehiclesAvailable = false;
    
    public static void initialize() {
        try {
            // Controlla se il plugin Vehicles è installato
            Plugin plugin = org.bukkit.Bukkit.getPluginManager().getPlugin("Vehicles");
            if (plugin != null && plugin.isEnabled()) {
                vehiclesPlugin = plugin;
                isVehiclesAvailable = true;
                System.out.println("[Factory] Vehicles plugin trovato e integrato!");
            }
        } catch (Exception e) {
            System.out.println("[Factory] Vehicles plugin non trovato, usando sistema simulato");
            isVehiclesAvailable = false;
        }
    }
    
    public static boolean isVehiclesAvailable() {
        return isVehiclesAvailable;
    }
    
    public static boolean giveVehicle(Player player, String vehicleType) {
        if (!isVehiclesAvailable) {
            return false;
        }
        
        try {
            // Comando per dare veicolo dal plugin Vehicles
            // Format: vehicles give <player> <vehicle>
            String command = "vehicles give " + player.getName() + " " + vehicleType;
            org.bukkit.Bukkit.dispatchCommand(org.bukkit.Bukkit.getConsoleSender(), command);
            return true;
        } catch (Exception e) {
            System.out.println("[Factory] Errore nel dare veicolo: " + e.getMessage());
            return false;
        }
    }
    
    public static boolean giveFuel(Player player, String fuelType, int amount) {
        if (!isVehiclesAvailable) {
            return false;
        }
        
        try {
            // Comando per dare carburante dal plugin Vehicles
            // Format: vehicles fuel <player> <fuel_type> <amount>
            String command = "vehicles fuel " + player.getName() + " " + fuelType + " " + amount;
            org.bukkit.Bukkit.dispatchCommand(org.bukkit.Bukkit.getConsoleSender(), command);
            return true;
        } catch (Exception e) {
            System.out.println("[Factory] Errore nel dare carburante: " + e.getMessage());
            return false;
        }
    }
    
    public static ItemStack getVehicleItem(String vehicleType) {
        if (!isVehiclesAvailable) {
            // Fallback a item simulato
            return createSimulatedVehicleItem(vehicleType);
        }
        
        try {
            // Prova a ottenere l'item reale dal plugin Vehicles
            // Questo dipende dall'API specifica del plugin Vehicles
            return createSimulatedVehicleItem(vehicleType); // Fallback per ora
        } catch (Exception e) {
            return createSimulatedVehicleItem(vehicleType);
        }
    }
    
    private static ItemStack createSimulatedVehicleItem(String vehicleType) {
        Material material = switch (vehicleType.toLowerCase()) {
            case "car" -> Material.MINECART;
            case "motorcycle" -> Material.MINECART;
            case "truck" -> Material.CHEST_MINECART;
            case "helicopter" -> Material.HOPPER_MINECART;
            case "boat" -> Material.OAK_BOAT;
            case "plane" -> Material.FURNACE_MINECART;
            default -> Material.MINECART;
        };
        
        ItemStack item = new ItemStack(material);
        var meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(net.kyori.adventure.text.Component.text("§b" + formatVehicleName(vehicleType)));
            meta.lore(java.util.List.of(
                net.kyori.adventure.text.Component.text("§7Veicolo del plugin Vehicles"),
                net.kyori.adventure.text.Component.text("§7Click per spawnare"),
                net.kyori.adventure.text.Component.text(""),
                net.kyori.adventure.text.Component.text("§7Richiede: §e" + getRequiredFuel(vehicleType))
            ));
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    private static String formatVehicleName(String vehicleType) {
        return switch (vehicleType.toLowerCase()) {
            case "car" -> "Automobile";
            case "motorcycle" -> "Motocicletta";
            case "truck" -> "Camion";
            case "helicopter" -> "Elicottero";
            case "boat" -> "Barca";
            case "plane" -> "Aereo";
            default -> vehicleType;
        };
    }
    
    private static String getRequiredFuel(String vehicleType) {
        return switch (vehicleType.toLowerCase()) {
            case "car", "motorcycle", "boat" -> "Benzina";
            case "truck" -> "Diesel";
            case "helicopter", "plane" -> "Carburante Aviazione";
            default -> "Benzina";
        };
    }
    
    public static String[] getAvailableVehicles() {
        if (!isVehiclesAvailable) {
            return new String[]{"car", "motorcycle", "truck", "helicopter", "boat", "plane"};
        }
        
        try {
            // Prova a ottenere la lista reale dal plugin Vehicles
            return new String[]{"car", "motorcycle", "truck", "helicopter", "boat", "plane"};
        } catch (Exception e) {
            return new String[]{"car", "motorcycle", "truck", "helicopter", "boat", "plane"};
        }
    }
    
    public static String[] getAvailableFuels() {
        if (!isVehiclesAvailable) {
            return new String[]{"gasoline", "diesel", "aviation_fuel"};
        }
        
        try {
            // Prova a ottenere la lista reale dal plugin Vehicles
            return new String[]{"gasoline", "diesel", "aviation_fuel"};
        } catch (Exception e) {
            return new String[]{"gasoline", "diesel", "aviation_fuel"};
        }
    }
}

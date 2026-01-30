package org.RisingSMP.factory.production;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;
import org.RisingSMP.factory.vehicles.VehiclesIntegration;

import java.util.HashMap;
import java.util.Map;

public class VehicleProduction {
    
    public enum VehicleType {
        CAR(Material.IRON_BLOCK, 12, "car", "Automobile", 45),
        MOTORCYCLE(Material.IRON_INGOT, 8, "motorcycle", "Motocicletta", 30),
        TRUCK(Material.IRON_BLOCK, 20, "truck", "Camion", 60),
        HELICOPTER(Material.IRON_BLOCK, 25, "helicopter", "Elicottero", 80),
        BOAT(Material.OAK_PLANKS, 6, "boat", "Barca", 20),
        PLANE(Material.IRON_BLOCK, 30, "plane", "Aereo", 100);
        
        private final Material material;
        private final int amount;
        private final String vehicleId;
        private final String displayName;
        private final int productionTime;
        
        VehicleType(Material material, int amount, String vehicleId, String displayName, int productionTime) {
            this.material = material;
            this.amount = amount;
            this.vehicleId = vehicleId;
            this.displayName = displayName;
            this.productionTime = productionTime;
        }
        
        public Material getMaterial() { return material; }
        public int getAmount() { return amount; }
        public String getVehicleId() { return vehicleId; }
        public String getDisplayName() { return displayName; }
        public int getProductionTime() { return productionTime; }
    }
    
    public enum FuelType {
        GASOLINE(Material.COAL, 3, "gasoline", "Benzina", 5),
        DIESEL(Material.COAL_BLOCK, 1, "diesel", "Diesel", 8),
        AVIATION_FUEL(Material.BLAZE_POWDER, 2, "aviation_fuel", "Carburante Aviazione", 12);
        
        private final Material material;
        private final int amount;
        private final String fuelId;
        private final String displayName;
        private final int productionTime;
        
        FuelType(Material material, int amount, String fuelId, String displayName, int productionTime) {
            this.material = material;
            this.amount = amount;
            this.fuelId = fuelId;
            this.displayName = displayName;
            this.productionTime = productionTime;
        }
        
        public Material getMaterial() { return material; }
        public int getAmount() { return amount; }
        public String getFuelId() { return fuelId; }
        public String getDisplayName() { return displayName; }
        public int getProductionTime() { return productionTime; }
    }
    
    private static final Map<VehicleType, ItemStack> vehicleCache = new HashMap<>();
    private static final Map<FuelType, ItemStack> fuelCache = new HashMap<>();
    
    static {
        // Inizializza cache veicoli
        for (VehicleType type : VehicleType.values()) {
            ItemStack vehicle = createVehicle(type);
            vehicleCache.put(type, vehicle);
        }
        
        // Inizializza cache carburanti
        for (FuelType type : FuelType.values()) {
            ItemStack fuel = createFuel(type);
            fuelCache.put(type, fuel);
        }
    }
    
    private static ItemStack createVehicle(VehicleType type) {
        // Usa l'integrazione con Vehicles plugin se disponibile
        if (VehiclesIntegration.isVehiclesAvailable()) {
            return VehiclesIntegration.getVehicleItem(type.getVehicleId());
        }
        
        // Fallback a item simulato
        Material vehicleMaterial = switch (type) {
            case CAR -> Material.MINECART;
            case MOTORCYCLE -> Material.MINECART;
            case TRUCK -> Material.CHEST_MINECART;
            case HELICOPTER -> Material.HOPPER_MINECART;
            case BOAT -> Material.OAK_BOAT;
            case PLANE -> Material.FURNACE_MINECART;
        };
        
        ItemStack vehicle = new ItemStack(vehicleMaterial);
        ItemMeta meta = vehicle.getItemMeta();
        
        if (meta != null) {
            meta.displayName(net.kyori.adventure.text.Component.text("§b" + type.getDisplayName()));
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            
            // Statistiche veicolo
            String speed = switch (type) {
                case MOTORCYCLE -> "§aAlta";
                case CAR -> "§eMedia";
                case TRUCK -> "§cBassa";
                case HELICOPTER, PLANE -> "§6Molto Alta";
                case BOAT -> "§eMedia";
            };
            
            String capacity = switch (type) {
                case MOTORCYCLE -> "§e1 posto";
                case CAR -> "§a4 posti";
                case TRUCK -> "§62 posti + cargo";
                case HELICOPTER -> "§a4 posti";
                case PLANE -> "§a6 posti";
                case BOAT -> "§e2 posti";
            };
            
            meta.lore(java.util.List.of(
                net.kyori.adventure.text.Component.text("§7Velocità: " + speed),
                net.kyori.adventure.text.Component.text("§7Capacità: " + capacity),
                net.kyori.adventure.text.Component.text("§7Autonomia: §e" + (type.ordinal() + 2) + " ore"),
                net.kyori.adventure.text.Component.text("§7Tempo produzione: §e" + type.getProductionTime() + "s"),
                net.kyori.adventure.text.Component.text(""),
                net.kyori.adventure.text.Component.text(
                    VehiclesIntegration.isVehiclesAvailable() ? 
                        "§7Veicolo reale del plugin Vehicles" :
                        "§7Click destro per spawnare (simulato)"
                )
            ));
            
            vehicle.setItemMeta(meta);
        }
        
        return vehicle;
    }
    
    private static ItemStack createFuel(FuelType type) {
        // Simula carburante con materiali appropriati
        Material fuelMaterial = switch (type) {
            case GASOLINE -> Material.COAL;
            case DIESEL -> Material.COAL_BLOCK;
            case AVIATION_FUEL -> Material.BLAZE_POWDER;
        };
        
        ItemStack fuel = new ItemStack(fuelMaterial, type.getAmount());
        ItemMeta meta = fuel.getItemMeta();
        
        if (meta != null) {
            meta.displayName(net.kyori.adventure.text.Component.text("§6" + type.getDisplayName()));
            meta.lore(java.util.List.of(
                net.kyori.adventure.text.Component.text("§7Quantità: §a" + type.getAmount()),
                net.kyori.adventure.text.Component.text("§7Potenza: §e" + (type.ordinal() + 1) + " unità"),
                net.kyori.adventure.text.Component.text("§7Tempo produzione: §e" + type.getProductionTime() + "s"),
                net.kyori.adventure.text.Component.text(""),
                net.kyori.adventure.text.Component.text("§7Compatibile con: " + getCompatibleVehicles(type))
            ));
            fuel.setItemMeta(meta);
        }
        
        return fuel;
    }
    
    private static String getCompatibleVehicles(FuelType fuelType) {
        return switch (fuelType) {
            case GASOLINE -> "§aAuto, Moto, Barca";
            case DIESEL -> "§eCamion";
            case AVIATION_FUEL -> "§bElicottero, Aereo";
        };
    }
    
    public static ItemStack getVehicle(VehicleType type) {
        return vehicleCache.get(type).clone();
    }
    
    public static ItemStack getFuel(FuelType type) {
        return fuelCache.get(type).clone();
    }
    
    public static boolean canProduceVehicle(VehicleType type, ItemStack[] ingredients) {
        int required = type.getAmount();
        
        for (ItemStack item : ingredients) {
            if (item != null && item.getType() == type.getMaterial()) {
                required -= item.getAmount();
                if (required <= 0) return true;
            }
        }
        
        return false;
    }
    
    public static boolean canProduceFuel(FuelType type, ItemStack[] ingredients) {
        int required = type.getAmount();
        
        for (ItemStack item : ingredients) {
            if (item != null && item.getType() == type.getMaterial()) {
                required -= item.getAmount();
                if (required <= 0) return true;
            }
        }
        
        return false;
    }
    
    public static int getVehicleProductionCost(VehicleType type) {
        return type.getProductionTime() * 3; // Costo energetico elevato
    }
    
    public static int getFuelProductionCost(FuelType type) {
        return type.getProductionTime(); // Costo energetico moderato
    }
    
    public static FuelType getRequiredFuel(VehicleType vehicle) {
        return switch (vehicle) {
            case CAR, MOTORCYCLE, BOAT -> FuelType.GASOLINE;
            case TRUCK -> FuelType.DIESEL;
            case HELICOPTER, PLANE -> FuelType.AVIATION_FUEL;
        };
    }
    
    public static boolean giveVehicleToPlayer(org.bukkit.entity.Player player, VehicleType type) {
        if (VehiclesIntegration.isVehiclesAvailable()) {
            return VehiclesIntegration.giveVehicle(player, type.getVehicleId());
        }
        
        // Fallback: give simulated item
        player.getInventory().addItem(getVehicle(type));
        return true;
    }
    
    public static boolean giveFuelToPlayer(org.bukkit.entity.Player player, FuelType type, int amount) {
        if (VehiclesIntegration.isVehiclesAvailable()) {
            return VehiclesIntegration.giveFuel(player, type.getFuelId(), amount);
        }
        
        // Fallback: give simulated items
        ItemStack fuel = getFuel(type);
        fuel.setAmount(amount);
        player.getInventory().addItem(fuel);
        return true;
    }
}

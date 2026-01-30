package org.RisingSMP.factory.permissions;

import org.bukkit.entity.Player;
import org.RisingSMP.factory.Factory;
import org.RisingSMP.factory.config.FactoryConfig;

public class PermissionManager {
    
    private static boolean vaultEnabled = false;
    
    public static void initialize() {
        // Check for Vault plugin
        if (Factory.getInstance().getServer().getPluginManager().getPlugin("Vault") == null) {
            Factory.getInstance().getLogger().warning("Vault not found! Using default permissions.");
            vaultEnabled = false;
            return;
        }
        
        vaultEnabled = true;
        Factory.getInstance().getLogger().info("Vault permissions hooked!");
    }
    
    // Permission checks
    public static boolean hasPermission(Player player, String perm) {
        if (!vaultEnabled) {
            return hasDefaultPermission(player, perm);
        }
        
        return player.hasPermission(perm);
    }
    
    public static boolean hasFactoryUse(Player player) {
        return hasPermission(player, "factory.use");
    }
    
    public static boolean hasFactoryAdmin(Player player) {
        return hasPermission(player, "factory.admin") || player.isOp();
    }
    
    public static boolean hasFactoryUpgrade(Player player) {
        return hasPermission(player, "factory.upgrade");
    }
    
    public static boolean hasFactoryVehicles(Player player) {
        return hasPermission(player, "factory.vehicles");
    }
    
    public static boolean hasFactoryWeapons(Player player) {
        return hasPermission(player, "factory.weapons");
    }
    
    public static boolean hasFactoryEnergy(Player player) {
        return hasPermission(player, "factory.energy");
    }
    
    public static boolean hasFactoryConfig(Player player) {
        return hasPermission(player, "factory.config");
    }
    
    public static boolean hasFactoryBypass(Player player) {
        return hasPermission(player, "factory.bypass") || hasFactoryAdmin(player);
    }
    
    // Economy methods (simplified without Vault)
    public static boolean hasEconomy() {
        return false; // Simplified for now
    }
    
    public static double getMoney(Player player) {
        return 0.0;
    }
    
    public static boolean withdrawMoney(Player player, double amount) {
        return false;
    }
    
    public static boolean depositMoney(Player player, double amount) {
        return false;
    }
    
    public static String formatMoney(double amount) {
        return "$" + String.format("%.2f", amount);
    }
    
    // Default permissions (when Vault is not available)
    private static boolean hasDefaultPermission(Player player, String perm) {
        // Default to OP-based permissions if Vault is not available
        return switch (perm) {
            case "factory.use" -> true; // Everyone can use basic factory
            case "factory.upgrade" -> player.isOp();
            case "factory.admin" -> player.isOp();
            case "factory.vehicles" -> true; // Everyone can use vehicles
            case "factory.weapons" -> true; // Everyone can use weapons
            case "factory.energy" -> true; // Everyone can use energy
            case "factory.config" -> player.isOp();
            case "factory.bypass" -> player.isOp();
            default -> false;
        };
    }
    
    // Permission info
    public static boolean isVaultEnabled() {
        return vaultEnabled;
    }
    
    public static String getPermissionInfo(Player player) {
        StringBuilder info = new StringBuilder();
        info.append("§6=== Permessi Factory ===\n");
        info.append("§7Vault: ").append(vaultEnabled ? "§aConnesso" : "§cNon disponibile").append("\n");
        info.append("§7Economy: §cNon disponibile\n");
        info.append("\n§6Permessi Giocatore:\n");
        
        info.append("§7- Use: ").append(hasFactoryUse(player) ? "§a✓" : "§c✗").append("\n");
        info.append("§7- Upgrade: ").append(hasFactoryUpgrade(player) ? "§a✓" : "§c✗").append("\n");
        info.append("§7- Vehicles: ").append(hasFactoryVehicles(player) ? "§a✓" : "§c✗").append("\n");
        info.append("§7- Weapons: ").append(hasFactoryWeapons(player) ? "§a✓" : "§c✗").append("\n");
        info.append("§7- Energy: ").append(hasFactoryEnergy(player) ? "§a✓" : "§c✗").append("\n");
        info.append("§7- Config: ").append(hasFactoryConfig(player) ? "§a✓" : "§c✗").append("\n");
        info.append("§7- Admin: ").append(hasFactoryAdmin(player) ? "§a✓" : "§c✗").append("\n");
        
        return info.toString();
    }
    
    // Machine-specific permissions
    public static boolean canUseMachineType(Player player, String machineType) {
        return hasPermission(player, "factory.machine." + machineType.toLowerCase());
    }
    
    public static boolean canBypassMachineLimit(Player player) {
        return hasPermission(player, "factory.bypass.limit");
    }
    
    public static boolean canBypassEnergyCost(Player player) {
        return hasPermission(player, "factory.bypass.energy");
    }
    
    public static boolean canBypassUpgradeCost(Player player) {
        return hasPermission(player, "factory.bypass.upgrade");
    }
    
    // Production permissions
    public static boolean canProduceWeapon(Player player, String weaponType) {
        return hasPermission(player, "factory.produce.weapon." + weaponType.toLowerCase());
    }
    
    public static boolean canProduceVehicle(Player player, String vehicleType) {
        return hasPermission(player, "factory.produce.vehicle." + vehicleType.toLowerCase());
    }
    
    public static boolean canProduceFuel(Player player, String fuelType) {
        return hasPermission(player, "factory.produce.fuel." + fuelType.toLowerCase());
    }
}

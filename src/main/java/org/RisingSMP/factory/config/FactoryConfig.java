package org.RisingSMP.factory.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FactoryConfig {
    
    private static FileConfiguration config;
    private static File configFile;
    
    public static void loadConfig(JavaPlugin plugin) {
        configFile = new File(plugin.getDataFolder(), "config.yml");
        
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        
        config = YamlConfiguration.loadConfiguration(configFile);
        
        // Load default values if missing
        loadDefaults();
    }
    
    private static void loadDefaults() {
        // Machine settings
        config.addDefault("machines.max_level", 10);
        config.addDefault("machines.upgrade_costs", List.of(
            10, 25, 50, 100, 200, 400, 800, 1600, 3200, 6400
        ));
        
        // Energy settings
        config.addDefault("energy.distribution_interval", 20);
        config.addDefault("energy.buffer_capacity", 1000);
        config.addDefault("energy.enable_system", true);
        
        // Vehicles settings
        config.addDefault("vehicles.enable_integration", true);
        config.addDefault("vehicles.require_fuel", true);
        config.addDefault("vehicles.fuel_consumption_rate", 1.0);
        
        // Production settings
        config.addDefault("production.enable_weapons", true);
        config.addDefault("production.enable_vehicles", true);
        config.addDefault("production.production_speed_multiplier", 1.0);
        
        // GUI settings
        config.addDefault("gui.enable_animations", true);
        config.addDefault("gui.enable_sounds", true);
        config.addDefault("gui.update_interval", 5);
        
        // Notification settings
        config.addDefault("notifications.enable", true);
        config.addDefault("notifications.cooldown_ms", 3000);
        config.addDefault("notifications.sound_volume", 0.8);
        
        // Performance settings
        config.addDefault("performance.enable_optimization", true);
        config.addDefault("performance.max_concurrent_machines", 100);
        config.addDefault("performance.cleanup_interval", 6000);
        
        // Debug settings
        config.addDefault("debug.enable_logging", false);
        config.addDefault("debug.verbose", false);
        
        saveConfig();
    }
    
    public static void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }
    
    // Machine settings
    public static int getMaxMachineLevel() {
        return config.getInt("machines.max_level", 10);
    }
    
    public static List<Integer> getUpgradeCosts() {
        return config.getIntegerList("machines.upgrade_costs");
    }
    
    public static int getUpgradeCost(int level) {
        List<Integer> costs = getUpgradeCosts();
        if (level <= 0 || level > costs.size()) {
            return costs.get(costs.size() - 1);
        }
        return costs.get(level - 1);
    }
    
    // Energy settings
    public static int getEnergyDistributionInterval() {
        return config.getInt("energy.distribution_interval", 20);
    }
    
    public static int getEnergyBufferCapacity() {
        return config.getInt("energy.buffer_capacity", 1000);
    }
    
    public static boolean isEnergySystemEnabled() {
        return config.getBoolean("energy.enable_system", true);
    }
    
    // Vehicles settings
    public static boolean isVehiclesIntegrationEnabled() {
        return config.getBoolean("vehicles.enable_integration", true);
    }
    
    public static boolean isVehicleFuelRequired() {
        return config.getBoolean("vehicles.require_fuel", true);
    }
    
    public static double getFuelConsumptionRate() {
        return config.getDouble("vehicles.fuel_consumption_rate", 1.0);
    }
    
    // Production settings
    public static boolean isWeaponProductionEnabled() {
        return config.getBoolean("production.enable_weapons", true);
    }
    
    public static boolean isVehicleProductionEnabled() {
        return config.getBoolean("production.enable_vehicles", true);
    }
    
    public static double getProductionSpeedMultiplier() {
        return config.getDouble("production.production_speed_multiplier", 1.0);
    }
    
    // GUI settings
    public static boolean isGUIAnimationsEnabled() {
        return config.getBoolean("gui.enable_animations", true);
    }
    
    public static boolean isGUISoundsEnabled() {
        return config.getBoolean("gui.enable_sounds", true);
    }
    
    public static int getGUIUpdateInterval() {
        return config.getInt("gui.update_interval", 5);
    }
    
    // Notification settings
    public static boolean areNotificationsEnabled() {
        return config.getBoolean("notifications.enable", true);
    }
    
    public static long getNotificationCooldown() {
        return config.getLong("notifications.cooldown_ms", 3000);
    }
    
    public static float getNotificationSoundVolume() {
        return (float) config.getDouble("notifications.sound_volume", 0.8);
    }
    
    // Performance settings
    public static boolean isPerformanceOptimizationEnabled() {
        return config.getBoolean("performance.enable_optimization", true);
    }
    
    public static int getMaxConcurrentMachines() {
        return config.getInt("performance.max_concurrent_machines", 100);
    }
    
    public static int getCleanupInterval() {
        return config.getInt("performance.cleanup_interval", 6000);
    }
    
    // Debug settings
    public static boolean isDebugLoggingEnabled() {
        return config.getBoolean("debug.enable_logging", false);
    }
    
    public static boolean isVerboseDebugEnabled() {
        return config.getBoolean("debug.verbose", false);
    }
    
    // Utility methods
    public static void set(String path, Object value) {
        config.set(path, value);
        saveConfig();
    }
    
    public static Object get(String path) {
        return config.get(path);
    }
    
    public static boolean getBoolean(String path, boolean def) {
        return config.getBoolean(path, def);
    }
    
    public static int getInt(String path, int def) {
        return config.getInt(path, def);
    }
    
    public static double getDouble(String path, double def) {
        return config.getDouble(path, def);
    }
    
    public static String getString(String path, String def) {
        return config.getString(path, def);
    }
    
    public static List<String> getStringList(String path) {
        return config.getStringList(path);
    }
    
    public static List<Integer> getIntegerList(String path) {
        return config.getIntegerList(path);
    }
}

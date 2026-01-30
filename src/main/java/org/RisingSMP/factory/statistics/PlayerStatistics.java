package org.RisingSMP.factory.statistics;

import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerStatistics {
    
    private static final Map<UUID, PlayerStats> playerStats = new ConcurrentHashMap<>();
    private static File statsFile;
    private static FileConfiguration statsConfig;
    
    public static class PlayerStats {
        private final UUID playerId;
        private final String playerName;
        
        // Production statistics
        private long totalWeaponsProduced = 0;
        private long totalVehiclesProduced = 0;
        private long totalFuelProduced = 0;
        
        // Machine statistics
        private int machinesOwned = 0;
        private int totalUpgrades = 0;
        private long totalEnergyGenerated = 0;
        
        // Time statistics
        private long firstJoinTime = 0;
        private long lastActiveTime = 0;
        private long totalPlayTime = 0;
        
        // Achievement progress
        private final Map<String, Integer> achievements = new HashMap<>();
        
        public PlayerStats(UUID playerId, String playerName) {
            this.playerId = playerId;
            this.playerName = playerName;
            this.firstJoinTime = System.currentTimeMillis();
            this.lastActiveTime = System.currentTimeMillis();
        }
        
        // Getters and setters
        public UUID getPlayerId() { return playerId; }
        public String getPlayerName() { return playerName; }
        
        public long getTotalWeaponsProduced() { return totalWeaponsProduced; }
        public void addWeaponsProduced(long amount) { totalWeaponsProduced += amount; }
        
        public long getTotalVehiclesProduced() { return totalVehiclesProduced; }
        public void addVehiclesProduced(long amount) { totalVehiclesProduced += amount; }
        
        public long getTotalFuelProduced() { return totalFuelProduced; }
        public void addFuelProduced(long amount) { totalFuelProduced += amount; }
        
        public int getMachinesOwned() { return machinesOwned; }
        public void incrementMachinesOwned() { machinesOwned++; }
        
        public int getTotalUpgrades() { return totalUpgrades; }
        public void incrementUpgrades() { totalUpgrades++; }
        
        public long getTotalEnergyGenerated() { return totalEnergyGenerated; }
        public void addEnergyGenerated(long amount) { totalEnergyGenerated += amount; }
        
        public long getLastActiveTime() { return lastActiveTime; }
        public void updateLastActive() { 
            long now = System.currentTimeMillis();
            totalPlayTime += (now - lastActiveTime);
            lastActiveTime = now;
        }
        
        public long getTotalPlayTime() { return totalPlayTime; }
        
        public Map<String, Integer> getAchievements() { return new HashMap<>(achievements); }
        public void setAchievement(String achievement, int progress) { achievements.put(achievement, progress); }
        public int getAchievementProgress(String achievement) { return achievements.getOrDefault(achievement, 0); }
        
        // Utility methods
        public long getTotalItemsProduced() {
            return totalWeaponsProduced + totalVehiclesProduced + totalFuelProduced;
        }
        
        public String getFormattedPlayTime() {
            long hours = totalPlayTime / (1000 * 60 * 60);
            long minutes = (totalPlayTime % (1000 * 60 * 60)) / (1000 * 60);
            return String.format("%dh %dm", hours, minutes);
        }
        
        public int getPlayerLevel() {
            // Simple level calculation based on total production
            long totalProduction = getTotalItemsProduced();
            if (totalProduction < 100) return 1;
            if (totalProduction < 500) return 2;
            if (totalProduction < 2000) return 3;
            if (totalProduction < 10000) return 4;
            if (totalProduction < 50000) return 5;
            return 6; // Max level
        }
        
        public long getExperienceToNextLevel() {
            int currentLevel = getPlayerLevel();
            if (currentLevel >= 6) return 0;
            
            long[] thresholds = {100, 500, 2000, 10000, 50000};
            long currentThreshold = currentLevel > 1 ? thresholds[currentLevel - 2] : 0;
            long nextThreshold = thresholds[currentLevel - 1];
            
            return Math.max(0, nextThreshold - getTotalItemsProduced());
        }
    }
    
    public static void initialize(JavaPlugin plugin) {
        statsFile = new File(plugin.getDataFolder(), "player_stats.yml");
        statsConfig = YamlConfiguration.loadConfiguration(statsFile);
        
        // Load existing statistics
        loadStatistics();
        
        plugin.getLogger().info("Player statistics system initialized");
    }
    
    private static void loadStatistics() {
        if (!statsConfig.contains("players")) return;
        
        for (String key : statsConfig.getConfigurationSection("players").getKeys(false)) {
            try {
                UUID playerId = UUID.fromString(key);
                String playerName = statsConfig.getString("players." + key + ".name", "Unknown");
                
                PlayerStats stats = new PlayerStats(playerId, playerName);
                
                // Load production stats
                stats.totalWeaponsProduced = statsConfig.getLong("players." + key + ".weapons", 0);
                stats.totalVehiclesProduced = statsConfig.getLong("players." + key + ".vehicles", 0);
                stats.totalFuelProduced = statsConfig.getLong("players." + key + ".fuel", 0);
                
                // Load machine stats
                stats.machinesOwned = statsConfig.getInt("players." + key + ".machines", 0);
                stats.totalUpgrades = statsConfig.getInt("players." + key + ".upgrades", 0);
                stats.totalEnergyGenerated = statsConfig.getLong("players." + key + ".energy", 0);
                
                // Load time stats
                stats.firstJoinTime = statsConfig.getLong("players." + key + ".first_join", System.currentTimeMillis());
                stats.lastActiveTime = statsConfig.getLong("players." + key + ".last_active", System.currentTimeMillis());
                stats.totalPlayTime = statsConfig.getLong("players." + key + ".play_time", 0);
                
                // Load achievements
                if (statsConfig.contains("players." + key + ".achievements")) {
                    for (String achievement : statsConfig.getConfigurationSection("players." + key + ".achievements").getKeys(false)) {
                        stats.achievements.put(achievement, statsConfig.getInt("players." + key + ".achievements." + achievement));
                    }
                }
                
                playerStats.put(playerId, stats);
            } catch (Exception e) {
                // Skip invalid entries
            }
        }
    }
    
    public static void saveStatistics() {
        for (Map.Entry<UUID, PlayerStats> entry : playerStats.entrySet()) {
            UUID playerId = entry.getKey();
            PlayerStats stats = entry.getValue();
            
            String path = "players." + playerId.toString();
            statsConfig.set(path + ".name", stats.getPlayerName());
            statsConfig.set(path + ".weapons", stats.getTotalWeaponsProduced());
            statsConfig.set(path + ".vehicles", stats.getTotalVehiclesProduced());
            statsConfig.set(path + ".fuel", stats.getTotalFuelProduced());
            statsConfig.set(path + ".machines", stats.getMachinesOwned());
            statsConfig.set(path + ".upgrades", stats.getTotalUpgrades());
            statsConfig.set(path + ".energy", stats.getTotalEnergyGenerated());
            statsConfig.set(path + ".first_join", stats.firstJoinTime);
            statsConfig.set(path + ".last_active", stats.getLastActiveTime());
            statsConfig.set(path + ".play_time", stats.getTotalPlayTime());
            
            // Save achievements
            for (Map.Entry<String, Integer> achievement : stats.getAchievements().entrySet()) {
                statsConfig.set(path + ".achievements." + achievement.getKey(), achievement.getValue());
            }
        }
        
        try {
            statsConfig.save(statsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static PlayerStats getPlayerStats(Player player) {
        return playerStats.computeIfAbsent(player.getUniqueId(), 
            k -> new PlayerStats(player.getUniqueId(), player.getName()));
    }
    
    public static PlayerStats getPlayerStats(UUID playerId) {
        return playerStats.get(playerId);
    }
    
    public static void recordWeaponProduction(Player player, long amount) {
        PlayerStats stats = getPlayerStats(player);
        stats.addWeaponsProduced(amount);
        stats.updateLastActive();
    }
    
    public static void recordVehicleProduction(Player player, long amount) {
        PlayerStats stats = getPlayerStats(player);
        stats.addVehiclesProduced(amount);
        stats.updateLastActive();
    }
    
    public static void recordFuelProduction(Player player, long amount) {
        PlayerStats stats = getPlayerStats(player);
        stats.addFuelProduced(amount);
        stats.updateLastActive();
    }
    
    public static void recordMachineCreation(Player player) {
        PlayerStats stats = getPlayerStats(player);
        stats.incrementMachinesOwned();
        stats.updateLastActive();
    }
    
    public static void recordMachineUpgrade(Player player) {
        PlayerStats stats = getPlayerStats(player);
        stats.incrementUpgrades();
        stats.updateLastActive();
    }
    
    public static void recordEnergyGeneration(Player player, long amount) {
        PlayerStats stats = getPlayerStats(player);
        stats.addEnergyGenerated(amount);
        stats.updateLastActive();
    }
    
    // Leaderboard methods
    public static Map<String, Long> getTopProducers(int limit) {
        Map<String, Long> topProducers = new HashMap<>();
        
        playerStats.entrySet().stream()
            .sorted((e1, e2) -> Long.compare(e2.getValue().getTotalItemsProduced(), e1.getValue().getTotalItemsProduced()))
            .limit(limit)
            .forEach(entry -> topProducers.put(entry.getValue().getPlayerName(), entry.getValue().getTotalItemsProduced()));
        
        return topProducers;
    }
    
    public static Map<String, Integer> getTopUpgraders(int limit) {
        Map<String, Integer> topUpgraders = new HashMap<>();
        
        playerStats.entrySet().stream()
            .sorted((e1, e2) -> Integer.compare(e2.getValue().getTotalUpgrades(), e1.getValue().getTotalUpgrades()))
            .limit(limit)
            .forEach(entry -> topUpgraders.put(entry.getValue().getPlayerName(), entry.getValue().getTotalUpgrades()));
        
        return topUpgraders;
    }
    
    public static Map<String, Long> getTopEnergyGenerators(int limit) {
        Map<String, Long> topGenerators = new HashMap<>();
        
        playerStats.entrySet().stream()
            .sorted((e1, e2) -> Long.compare(e2.getValue().getTotalEnergyGenerated(), e1.getValue().getTotalEnergyGenerated()))
            .limit(limit)
            .forEach(entry -> topGenerators.put(entry.getValue().getPlayerName(), entry.getValue().getTotalEnergyGenerated()));
        
        return topGenerators;
    }
    
    // Utility methods
    public static String getStatsSummary(Player player) {
        PlayerStats stats = getPlayerStats(player);
        
        StringBuilder summary = new StringBuilder();
        summary.append("§6=== Statistiche Factory ===\n");
        summary.append("§7Livello: §e").append(stats.getPlayerLevel()).append("\n");
        summary.append("§7Esperienza al prossimo livello: §e").append(stats.getExperienceToNextLevel()).append("\n");
        summary.append("§7Tempo di gioco: §a").append(stats.getFormattedPlayTime()).append("\n");
        summary.append("§7Macchine possedute: §b").append(stats.getMachinesOwned()).append("\n");
        summary.append("§7Upgrade totali: §d").append(stats.getTotalUpgrades()).append("\n");
        summary.append("§7Armi prodotte: §c").append(stats.getTotalWeaponsProduced()).append("\n");
        summary.append("§7Veicoli prodotti: §e").append(stats.getTotalVehiclesProduced()).append("\n");
        summary.append("§7Carburante prodotto: §6").append(stats.getTotalFuelProduced()).append("\n");
        summary.append("§7Energia generata: §a").append(stats.getTotalEnergyGenerated()).append("\n");
        summary.append("§7Oggetti totali prodotti: §b").append(stats.getTotalItemsProduced()).append("\n");
        
        return summary.toString();
    }
    
    public static void cleanupInactivePlayers(long inactiveDays) {
        long inactiveMillis = inactiveDays * 24 * 60 * 60 * 1000L;
        long cutoffTime = System.currentTimeMillis() - inactiveMillis;
        
        playerStats.entrySet().removeIf(entry -> entry.getValue().getLastActiveTime() < cutoffTime);
    }
}

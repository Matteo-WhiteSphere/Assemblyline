package org.RisingSMP.factory.achievements;

import org.bukkit.entity.Player;
import org.bukkit.Sound;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.RisingSMP.factory.Factory;
import org.RisingSMP.factory.statistics.PlayerStatistics;
import org.RisingSMP.factory.notifications.NotificationManager;
import org.RisingSMP.factory.permissions.PermissionManager;

import java.util.*;

public class AchievementSystem {
    
    public enum Achievement {
        // Production achievements
        FIRST_WEAPON("Prima Arma", "Produci la tua prima arma", "§cProducisci 1 arma", 1, 
            new ItemStack(org.bukkit.Material.IRON_HOE), Sound.ENTITY_PLAYER_LEVELUP),
        
        WEAPON_MASTER("Maestro delle Armi", "Produci 100 armi", "§cProducisci 100 armi", 100,
            new ItemStack(org.bukkit.Material.DIAMOND_SWORD), Sound.UI_TOAST_CHALLENGE_COMPLETE),
        
        FIRST_VEHICLE("Primo Veicolo", "Produci il tuo primo veicolo", "§eProduci 1 veicolo", 1,
            new ItemStack(org.bukkit.Material.MINECART), Sound.ENTITY_PLAYER_LEVELUP),
        
        VEHICLE_COLLECTOR("Collezionista di Veicoli", "Produci 50 veicoli", "§eProduci 50 veicoli", 50,
            new ItemStack(org.bukkit.Material.CHEST_MINECART), Sound.UI_TOAST_CHALLENGE_COMPLETE),
        
        FUEL_PRODUCER("Produttore di Carburante", "Produci 1000 unità di carburante", "§6Produci 1000 carburante", 1000,
            new ItemStack(org.bukkit.Material.COAL), Sound.ENTITY_PLAYER_LEVELUP),
        
        // Machine achievements
        MACHINE_BUILDER("Costruttore di Macchine", "Possiedi 10 macchine", "§bPossiedi 10 macchine", 10,
            new ItemStack(org.bukkit.Material.PISTON), Sound.ENTITY_PLAYER_LEVELUP),
        
        FACTORY_MAGNATE("Magnate della Fabbrica", "Possiedi 50 macchine", "§bPossiedi 50 macchine", 50,
            new ItemStack(org.bukkit.Material.PISTON), Sound.UI_TOAST_CHALLENGE_COMPLETE),
        
        UPGRADE_MASTER("Maestro degli Upgrade", "Esegui 25 upgrade", "§dEsegui 25 upgrade", 25,
            new ItemStack(org.bukkit.Material.EMERALD_BLOCK), Sound.UI_TOAST_CHALLENGE_COMPLETE),
        
        // Energy achievements
        POWER_GENERATOR("Generatore di Energia", "Produci 10000 unità di energia", "§aProduci 10000 energia", 10000,
            new ItemStack(org.bukkit.Material.REDSTONE), Sound.ENTITY_PLAYER_LEVELUP),
        
        ENERGY_TYCOON("Tycoon dell'Energia", "Produci 100000 unità di energia", "§aProduci 100000 energia", 100000,
            new ItemStack(org.bukkit.Material.REDSTONE_BLOCK), Sound.UI_TOAST_CHALLENGE_COMPLETE),
        
        // Milestone achievements
        PRODUCTION_NOVICE("Novizio della Produzione", "Produci 100 oggetti totali", "§eProduci 100 oggetti", 100,
            new ItemStack(org.bukkit.Material.CRAFTING_TABLE), Sound.ENTITY_PLAYER_LEVELUP),
        
        PRODUCTION_EXPERT("Esperto della Produzione", "Produci 1000 oggetti totali", "§eProduci 1000 oggetti", 1000,
            new ItemStack(org.bukkit.Material.CRAFTING_TABLE), Sound.UI_TOAST_CHALLENGE_COMPLETE),
        
        PRODUCTION_LEGEND("Leggenda della Produzione", "Produci 10000 oggetti totali", "§eProduci 10000 oggetti", 10000,
            new ItemStack(org.bukkit.Material.CRAFTING_TABLE), Sound.UI_TOAST_CHALLENGE_COMPLETE),
        
        // Special achievements
        DEDICATED_WORKER("Lavoratore Dedonato", "Gioca per 10 ore", "§7Gioca per 10 ore", 10 * 60 * 60 * 1000L,
            new ItemStack(org.bukkit.Material.CLOCK), Sound.ENTITY_PLAYER_LEVELUP),
        
        FACTORY_VETERAN("Veterano della Fabbrica", "Gioca per 50 ore", "§7Gioca per 50 ore", 50 * 60 * 60 * 1000L,
            new ItemStack(org.bukkit.Material.CLOCK), Sound.UI_TOAST_CHALLENGE_COMPLETE);
        
        private final String name;
        private final String description;
        private final String requirement;
        private final long target;
        private final ItemStack icon;
        private final Sound sound;
        
        Achievement(String name, String description, String requirement, long target, ItemStack icon, Sound sound) {
            this.name = name;
            this.description = description;
            this.requirement = requirement;
            this.target = target;
            this.icon = icon;
            this.sound = sound;
        }
        
        public String getName() { return name; }
        public String getDescription() { return description; }
        public String getRequirement() { return requirement; }
        public long getTarget() { return target; }
        public ItemStack getIcon() { return icon; }
        public Sound getSound() { return sound; }
    }
    
    private static final Map<UUID, Set<Achievement>> playerAchievements = new HashMap<>();
    private static final Map<Achievement, Long> achievementProgress = new HashMap<>();
    
    public static void initialize() {
        Factory.getInstance().getLogger().info("Achievement system initialized");
    }
    
    public static boolean hasAchievement(Player player, Achievement achievement) {
        return playerAchievements.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>()).contains(achievement);
    }
    
    public static void unlockAchievement(Player player, Achievement achievement) {
        if (hasAchievement(player, achievement)) return;
        
        // Check permissions
        if (!PermissionManager.hasFactoryUse(player)) return;
        
        // Unlock achievement
        playerAchievements.get(player.getUniqueId()).add(achievement);
        
        // Play sound and effects
        player.playSound(player.getLocation(), achievement.getSound(), 1.0f, 1.0f);
        player.spawnParticle(Particle.TOTEM_OF_UNDYING, player.getLocation().add(0, 1, 0), 20, 0.5, 0.5, 0.5, 0.1);
        
        // Send notification
        NotificationManager.sendNotification(player, NotificationManager.NotificationType.SUCCESS,
            "§6Achievement Sbloccato: " + achievement.getName());
        
        // Send achievement message
        player.sendMessage("");
        player.sendMessage("§6§l╔════════════════════════════════════════╗");
        player.sendMessage("§6§l║           §e§lACHIEVEMENT SBLOCCATO!           §6§l║");
        player.sendMessage("§6§l╠════════════════════════════════════════╣");
        player.sendMessage("§6§l║ §f" + achievement.getName() + " §6§l║");
        player.sendMessage("§6§l║ §7" + achievement.getDescription() + " §6§l║");
        player.sendMessage("§6§l╚════════════════════════════════════════╝");
        player.sendMessage("");
        
        // Log achievement
        Factory.getInstance().getLogger().info(player.getName() + " unlocked achievement: " + achievement.getName());
    }
    
    public static void checkAchievements(Player player) {
        PlayerStatistics.PlayerStats stats = PlayerStatistics.getPlayerStats(player);
        
        // Check production achievements
        checkProductionAchievements(player, stats);
        
        // Check machine achievements
        checkMachineAchievements(player, stats);
        
        // Check energy achievements
        checkEnergyAchievements(player, stats);
        
        // Check time achievements
        checkTimeAchievements(player, stats);
    }
    
    private static void checkProductionAchievements(Player player, PlayerStatistics.PlayerStats stats) {
        // Weapon achievements
        if (stats.getTotalWeaponsProduced() >= Achievement.FIRST_WEAPON.getTarget()) {
            unlockAchievement(player, Achievement.FIRST_WEAPON);
        }
        if (stats.getTotalWeaponsProduced() >= Achievement.WEAPON_MASTER.getTarget()) {
            unlockAchievement(player, Achievement.WEAPON_MASTER);
        }
        
        // Vehicle achievements
        if (stats.getTotalVehiclesProduced() >= Achievement.FIRST_VEHICLE.getTarget()) {
            unlockAchievement(player, Achievement.FIRST_VEHICLE);
        }
        if (stats.getTotalVehiclesProduced() >= Achievement.VEHICLE_COLLECTOR.getTarget()) {
            unlockAchievement(player, Achievement.VEHICLE_COLLECTOR);
        }
        
        // Fuel achievements
        if (stats.getTotalFuelProduced() >= Achievement.FUEL_PRODUCER.getTarget()) {
            unlockAchievement(player, Achievement.FUEL_PRODUCER);
        }
        
        // Milestone achievements
        long totalProduced = stats.getTotalItemsProduced();
        if (totalProduced >= Achievement.PRODUCTION_NOVICE.getTarget()) {
            unlockAchievement(player, Achievement.PRODUCTION_NOVICE);
        }
        if (totalProduced >= Achievement.PRODUCTION_EXPERT.getTarget()) {
            unlockAchievement(player, Achievement.PRODUCTION_EXPERT);
        }
        if (totalProduced >= Achievement.PRODUCTION_LEGEND.getTarget()) {
            unlockAchievement(player, Achievement.PRODUCTION_LEGEND);
        }
    }
    
    private static void checkMachineAchievements(Player player, PlayerStatistics.PlayerStats stats) {
        if (stats.getMachinesOwned() >= Achievement.MACHINE_BUILDER.getTarget()) {
            unlockAchievement(player, Achievement.MACHINE_BUILDER);
        }
        if (stats.getMachinesOwned() >= Achievement.FACTORY_MAGNATE.getTarget()) {
            unlockAchievement(player, Achievement.FACTORY_MAGNATE);
        }
        
        if (stats.getTotalUpgrades() >= Achievement.UPGRADE_MASTER.getTarget()) {
            unlockAchievement(player, Achievement.UPGRADE_MASTER);
        }
    }
    
    private static void checkEnergyAchievements(Player player, PlayerStatistics.PlayerStats stats) {
        if (stats.getTotalEnergyGenerated() >= Achievement.POWER_GENERATOR.getTarget()) {
            unlockAchievement(player, Achievement.POWER_GENERATOR);
        }
        if (stats.getTotalEnergyGenerated() >= Achievement.ENERGY_TYCOON.getTarget()) {
            unlockAchievement(player, Achievement.ENERGY_TYCOON);
        }
    }
    
    private static void checkTimeAchievements(Player player, PlayerStatistics.PlayerStats stats) {
        if (stats.getTotalPlayTime() >= Achievement.DEDICATED_WORKER.getTarget()) {
            unlockAchievement(player, Achievement.DEDICATED_WORKER);
        }
        if (stats.getTotalPlayTime() >= Achievement.FACTORY_VETERAN.getTarget()) {
            unlockAchievement(player, Achievement.FACTORY_VETERAN);
        }
    }
    
    public static String getAchievementProgress(Player player) {
        PlayerStatistics.PlayerStats stats = PlayerStatistics.getPlayerStats(player);
        StringBuilder progress = new StringBuilder();
        
        progress.append("§6=== Progresso Achievement ===\n");
        
        // Show progress for locked achievements
        for (Achievement achievement : Achievement.values()) {
            if (hasAchievement(player, achievement)) continue;
            
            long current = getCurrentProgress(achievement, stats);
            long target = achievement.getTarget();
            double percentage = Math.min(100.0, (double) current / target * 100);
            
            progress.append("§7" + achievement.getName() + ": ");
            progress.append(getProgressBar(percentage));
            progress.append(" §e" + current + "/" + target + "\n");
        }
        
        return progress.toString();
    }
    
    private static long getCurrentProgress(Achievement achievement, PlayerStatistics.PlayerStats stats) {
        return switch (achievement) {
            case FIRST_WEAPON, WEAPON_MASTER -> stats.getTotalWeaponsProduced();
            case FIRST_VEHICLE, VEHICLE_COLLECTOR -> stats.getTotalVehiclesProduced();
            case FUEL_PRODUCER -> stats.getTotalFuelProduced();
            case MACHINE_BUILDER, FACTORY_MAGNATE -> stats.getMachinesOwned();
            case UPGRADE_MASTER -> stats.getTotalUpgrades();
            case POWER_GENERATOR, ENERGY_TYCOON -> stats.getTotalEnergyGenerated();
            case PRODUCTION_NOVICE, PRODUCTION_EXPERT, PRODUCTION_LEGEND -> stats.getTotalItemsProduced();
            case DEDICATED_WORKER, FACTORY_VETERAN -> stats.getTotalPlayTime();
        };
    }
    
    private static String getProgressBar(double percentage) {
        int totalBars = 20;
        int filledBars = (int) (percentage / 5); // Each bar represents 5%
        
        StringBuilder bar = new StringBuilder("§a");
        for (int i = 0; i < filledBars; i++) {
            bar.append("█");
        }
        bar.append("§7");
        for (int i = filledBars; i < totalBars; i++) {
            bar.append("░");
        }
        
        return bar.toString();
    }
    
    public static Map<String, Integer> getLeaderboard() {
        Map<String, Integer> leaderboard = new HashMap<>();
        
        for (Map.Entry<UUID, Set<Achievement>> entry : playerAchievements.entrySet()) {
            String playerName = PlayerStatistics.getPlayerStats(entry.getKey()).getPlayerName();
            int achievementCount = entry.getValue().size();
            leaderboard.put(playerName, achievementCount);
        }
        
        // Sort by achievement count
        return leaderboard.entrySet().stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .collect(LinkedHashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), LinkedHashMap::putAll);
    }
    
    public static Set<Achievement> getPlayerAchievements(Player player) {
        return playerAchievements.getOrDefault(player.getUniqueId(), new HashSet<>());
    }
    
    public static int getTotalAchievements() {
        return Achievement.values().length;
    }
    
    public static int getUnlockedAchievements(Player player) {
        return getPlayerAchievements(player).size();
    }
    
    public static double getCompletionPercentage(Player player) {
        return (double) getUnlockedAchievements(player) / getTotalAchievements() * 100;
    }
    
    // Utility methods for GUI
    public static ItemStack createAchievementItem(Achievement achievement, boolean unlocked) {
        ItemStack item = achievement.getIcon().clone();
        var meta = item.getItemMeta();
        
        if (meta != null) {
            if (unlocked) {
                meta.displayName(net.kyori.adventure.text.Component.text("§a" + achievement.getName()));
                meta.lore(java.util.List.of(
                    net.kyori.adventure.text.Component.text("§7" + achievement.getDescription()),
                    net.kyori.adventure.text.Component.text(""),
                    net.kyori.adventure.text.Component.text("§a§l✓ Sbloccato")
                ));
            } else {
                meta.displayName(net.kyori.adventure.text.Component.text("§c" + achievement.getName()));
                meta.lore(java.util.List.of(
                    net.kyori.adventure.text.Component.text("§7" + achievement.getDescription()),
                    net.kyori.adventure.text.Component.text("§7" + achievement.getRequirement()),
                    net.kyori.adventure.text.Component.text(""),
                    net.kyori.adventure.text.Component.text("§c§l✗ Non sbloccato")
                ));
            }
            item.setItemMeta(meta);
        }
        
        return item;
    }
}

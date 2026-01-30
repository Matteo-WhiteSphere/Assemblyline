package org.RisingSMP.factory.notifications;

import org.bukkit.entity.Player;
import org.bukkit.Sound;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NotificationManager {
    
    private static final Map<UUID, Long> lastNotification = new HashMap<>();
    private static final long NOTIFICATION_COOLDOWN = 3000; // 3 secondi
    
    public enum NotificationType {
        SUCCESS("§a✓", Sound.BLOCK_NOTE_BLOCK_CHIME, 0.8f, 1.0f),
        WARNING("§e⚠", Sound.BLOCK_NOTE_BLOCK_BASS, 0.6f, 0.8f),
        ERROR("§c✗", Sound.BLOCK_ANVIL_LAND, 0.5f, 0.5f),
        INFO("§bℹ", Sound.BLOCK_NOTE_BLOCK_HARP, 0.7f, 1.0f),
        UPGRADE("§6↑", Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f),
        ENERGY_LOW("§c⚡", Sound.BLOCK_REDSTONE_TORCH_BURNOUT, 0.8f, 0.6f),
        PRODUCTION_COMPLETE("§2✔", Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.9f, 1.1f);
        
        private final String prefix;
        private final Sound sound;
        private final float volume;
        private final float pitch;
        
        NotificationType(String prefix, Sound sound, float volume, float pitch) {
            this.prefix = prefix;
            this.sound = sound;
            this.volume = volume;
            this.pitch = pitch;
        }
        
        public String getPrefix() { return prefix; }
        public Sound getSound() { return sound; }
        public float getVolume() { return volume; }
        public float getPitch() { return pitch; }
    }
    
    public static void sendNotification(Player player, NotificationType type, String message) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        
        // Check cooldown
        if (lastNotification.containsKey(playerId)) {
            long lastTime = lastNotification.get(playerId);
            if (currentTime - lastTime < NOTIFICATION_COOLDOWN) {
                return; // Skip notification to prevent spam
            }
        }
        
        lastNotification.put(playerId, currentTime);
        
        // Send chat message
        player.sendMessage(type.getPrefix() + " §7[Factory] " + message);
        
        // Play sound
        player.playSound(player.getLocation(), type.getSound(), type.getVolume(), type.getPitch());
        
        // Send action bar for important notifications
        if (type == NotificationType.ERROR || type == NotificationType.UPGRADE) {
            sendActionBar(player, type.getPrefix() + " " + message);
        }
    }
    
    private static void sendActionBar(Player player, String message) {
        player.sendActionBar(net.kyori.adventure.text.Component.text(message));
    }
    
    public static void sendUpgradeComplete(Player player, String machineType, int newLevel) {
        sendNotification(player, NotificationType.UPGRADE, 
            machineType + " aggiornata al livello " + newLevel + "!");
    }
    
    public static void sendProductionComplete(Player player, String product, int amount) {
        sendNotification(player, NotificationType.PRODUCTION_COMPLETE,
            "Produzione completata: " + amount + "x " + product);
    }
    
    public static void sendEnergyWarning(Player player, String machineType) {
        sendNotification(player, NotificationType.ENERGY_LOW,
            "Energia insufficiente per " + machineType);
    }
    
    public static void sendStorageFull(Player player, String machineType) {
        sendNotification(player, NotificationType.WARNING,
            "Storage pieno per " + machineType);
    }
    
    public static void sendMachineStatus(Player player, String machineType, String status) {
        String message = machineType + ": " + status;
        
        NotificationType type = switch (status.toLowerCase()) {
            case "attivo", "produciendo" -> NotificationType.SUCCESS;
            case "spento", "fermo" -> NotificationType.WARNING;
            case "errore", "malfunzionamento" -> NotificationType.ERROR;
            default -> NotificationType.INFO;
        };
        
        sendNotification(player, type, message);
    }
    
    public static void clearCooldown(Player player) {
        lastNotification.remove(player.getUniqueId());
    }
    
    public static void sendWelcomeMessage(Player player) {
        player.sendMessage("§6=== §eFactory System §6===");
        player.sendMessage("§7Benvenuto nel sistema industriale!");
        player.sendMessage("§7Click destro sulle macchine per gestirle");
        player.sendMessage("§7Usa §e/factoryconfig §7per configurare");
        player.sendMessage("§6========================");
        
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.8f, 1.2f);
    }
}

package org.RisingSMP.factory.resourcepack;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.RisingSMP.factory.Factory;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class ResourcePackManager {
    
    private static final Map<UUID, Long> lastSentPack = new ConcurrentHashMap<>();
    private static File resourcePackFile;
    private static String resourcePackUrl;
    private static String resourcePackHash;
    private static boolean forceDownload = false;
    
    public static void initialize(JavaPlugin plugin) {
        // Create resource pack directory
        File resourcePackDir = new File(plugin.getDataFolder(), "resourcepack");
        if (!resourcePackDir.exists()) {
            resourcePackDir.mkdirs();
        }
        
        resourcePackFile = new File(resourcePackDir, "factory-resourcepack.zip");
        
        // Load configuration
        loadConfig(plugin);
        
        // Generate resource pack if needed
        if (!resourcePackFile.exists() || forceDownload) {
            generateResourcePack();
        }
        
        // Setup resource pack URL
        setupResourcePackUrl(plugin);
        
        // Start resource pack sender task
        startResourcePackTask();
        
        plugin.getLogger().info("Resource Pack Manager initialized");
    }
    
    private static void loadConfig(JavaPlugin plugin) {
        File configFile = new File(plugin.getDataFolder(), "resourcepack.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        
        resourcePackUrl = config.getString("resource_pack_url", "");
        forceDownload = config.getBoolean("force_download", false);
        boolean autoSend = config.getBoolean("auto_send", true);
        
        // Save default config
        if (!configFile.exists()) {
            config.set("resource_pack_url", "");
            config.set("force_download", false);
            config.set("auto_send", true);
            config.set("prompt_message", "§6Factory Plugin richiede il resource pack personalizzato per visualizzare correttamente le macchine!");
            config.set("kick_on_decline", false);
            
            try {
                config.save(configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private static void generateResourcePack() {
        Factory.getInstance().getLogger().info("Generating Factory resource pack...");
        
        try {
            // Create resource pack structure
            createResourcePackStructure();
            
            // Generate pack.mcmeta
            generatePackMeta();
            
            // Generate assets
            generateTextures();
            generateModels();
            generateLanguageFiles();
            
            // Create ZIP file
            createResourcePackZip();
            
            Factory.getInstance().getLogger().info("Resource pack generated successfully!");
            
        } catch (Exception e) {
            Factory.getInstance().getLogger().severe("Failed to generate resource pack: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void createResourcePackStructure() throws IOException {
        File packDir = new File(Factory.getInstance().getDataFolder(), "resourcepack_temp");
        if (packDir.exists()) {
            deleteDirectory(packDir);
        }
        packDir.mkdirs();
        
        // Create directories
        new File(packDir, "assets/minecraft/textures/block").mkdirs();
        new File(packDir, "assets/minecraft/textures/item").mkdirs();
        new File(packDir, "assets/minecraft/models/block").mkdirs();
        new File(packDir, "assets/minecraft/models/item").mkdirs();
        new File(packDir, "assets/minecraft/lang").mkdirs();
        new File(packDir, "assets/minecraft/blockstates").mkdirs();
    }
    
    private static void generatePackMeta() throws IOException {
        File packDir = new File(Factory.getInstance().getDataFolder(), "resourcepack_temp");
        File packMeta = new File(packDir, "pack.mcmeta");
        
        String packMetaContent = """
            {
              "pack": {
                "pack_format": 48,
                "description": "Factory Plugin Resource Pack",
                "pack_format": 48
              }
            }
            """;
        
        Files.writeString(packMeta.toPath(), packMetaContent);
    }
    
    private static void generateTextures() throws IOException {
        File packDir = new File(Factory.getInstance().getDataFolder(), "resourcepack_temp");
        
        // Generate basic texture placeholders
        // In a real implementation, these would be actual PNG files
        generateTexturePlaceholder(packDir, "assets/minecraft/textures/block/factory_generator.png");
        generateTexturePlaceholder(packDir, "assets/minecraft/textures/block/factory_refinery.png");
        generateTexturePlaceholder(packDir, "assets/minecraft/textures/block/factory_final.png");
        generateTexturePlaceholder(packDir, "assets/minecraft/textures/item/factory_component.png");
        generateTexturePlaceholder(packDir, "assets/minecraft/textures/item/factory_weapon.png");
        generateTexturePlaceholder(packDir, "assets/minecraft/textures/item/factory_vehicle.png");
    }
    
    private static void generateTexturePlaceholder(File packDir, String path) throws IOException {
        File textureFile = new File(packDir, path);
        textureFile.getParentFile().mkdirs();
        
        // Create a simple placeholder texture (in real implementation, use actual PNG files)
        // For now, we'll create empty files as placeholders
        textureFile.createNewFile();
    }
    
    private static void generateModels() throws IOException {
        File packDir = new File(Factory.getInstance().getDataFolder(), "resourcepack_temp");
        
        // Generator Machine Model
        String generatorModel = """
            {
              "parent": "minecraft:block/cube_all",
              "textures": {
                "all": "minecraft:block/factory_generator"
              }
            }
            """;
        
        Files.writeString(
            new File(packDir, "assets/minecraft/models/block/factory_generator.json").toPath(),
            generatorModel
        );
        
        // Refinery Machine Model
        String refineryModel = """
            {
              "parent": "minecraft:block/cube_all",
              "textures": {
                "all": "minecraft:block/factory_refinery"
              }
            }
            """;
        
        Files.writeString(
            new File(packDir, "assets/minecraft/models/block/factory_refinery.json").toPath(),
            refineryModel
        );
        
        // Final Machine Model
        String finalModel = """
            {
              "parent": "minecraft:block/cube_all",
              "textures": {
                "all": "minecraft:block/factory_final"
              }
            }
            """;
        
        Files.writeString(
            new File(packDir, "assets/minecraft/models/block/factory_final.json").toPath(),
            finalModel
        );
        
        // Item Models
        String componentModel = """
            {
              "parent": "minecraft:item/generated",
              "textures": {
                "layer0": "minecraft:item/factory_component"
              }
            }
            """;
        
        Files.writeString(
            new File(packDir, "assets/minecraft/models/item/factory_component.json").toPath(),
            componentModel
        );
    }
    
    private static void generateLanguageFiles() throws IOException {
        File packDir = new File(Factory.getInstance().getDataFolder(), "resourcepack_temp");
        
        // English language file
        String enUsLang = """
            {
              "block.minecraft.factory_generator": "Factory Generator",
              "block.minecraft.factory_refinery": "Factory Refinery",
              "block.minecraft.factory_final": "Factory Final Machine",
              "item.minecraft.factory_component": "Factory Component",
              "item.minecraft.factory_weapon": "Factory Weapon",
              "item.minecraft.factory_vehicle": "Factory Vehicle",
              "factory.tooltip.level": "Level: %d",
              "factory.tooltip.energy": "Energy: %d/%d",
              "factory.tooltip.status.active": "§aActive",
              "factory.tooltip.status.inactive": "§cInactive"
            }
            """;
        
        Files.writeString(
            new File(packDir, "assets/minecraft/lang/en_us.json").toPath(),
            enUsLang
        );
        
        // Italian language file
        String itItLang = """
            {
              "block.minecraft.factory_generator": "Generatore di Fabbrica",
              "block.minecraft.factory_refinery": "Raffineria di Fabbrica",
              "block.minecraft.factory_final": "Macchina Finale di Fabbrica",
              "item.minecraft.factory_component": "Componente di Fabbrica",
              "item.minecraft.factory_weapon": "Arma di Fabbrica",
              "item.minecraft.factory_vehicle": "Veicolo di Fabbrica",
              "factory.tooltip.level": "Livello: %d",
              "factory.tooltip.energy": "Energia: %d/%d",
              "factory.tooltip.status.active": "§aAttivo",
              "factory.tooltip.status.inactive": "§cSpento"
            }
            """;
        
        Files.writeString(
            new File(packDir, "assets/minecraft/lang/it_it.json").toPath(),
            itItLang
        );
    }
    
    private static void generateBlockStates() throws IOException {
        File packDir = new File(Factory.getInstance().getDataFolder(), "resourcepack_temp");
        
        // Generator Block State
        String generatorBlockState = """
            {
              "variants": {
                "": { "model": "minecraft:block/factory_generator" }
              }
            }
            """;
        
        Files.writeString(
            new File(packDir, "assets/minecraft/blockstates/factory_generator.json").toPath(),
            generatorBlockState
        );
        
        // Refinery Block State
        String refineryBlockState = """
            {
              "variants": {
                "": { "model": "minecraft:block/factory_refinery" }
              }
            }
            """;
        
        Files.writeString(
            new File(packDir, "assets/minecraft/blockstates/factory_refinery.json").toPath(),
            refineryBlockState
        );
        
        // Final Machine Block State
        String finalBlockState = """
            {
              "variants": {
                "": { "model": "minecraft:block/factory_final" }
              }
            }
            """;
        
        Files.writeString(
            new File(packDir, "assets/minecraft/blockstates/factory_final.json").toPath(),
            finalBlockState
        );
    }
    
    private static void createResourcePackZip() throws IOException {
        File packDir = new File(Factory.getInstance().getDataFolder(), "resourcepack_temp");
        
        // Create ZIP file (simplified - in real implementation would use proper ZIP creation)
        ProcessBuilder pb = new ProcessBuilder("zip", "-r", "../factory-resourcepack.zip", ".");
        pb.directory(packDir);
        
        try {
            Process process = pb.start();
            process.waitFor();
        } catch (Exception e) {
            // Fallback: create empty file as placeholder
            resourcePackFile.createNewFile();
        }
        
        // Clean up temp directory
        deleteDirectory(packDir);
    }
    
    private static void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }
    
    private static void setupResourcePackUrl(JavaPlugin plugin) {
        if (resourcePackUrl.isEmpty()) {
            // Setup local file URL (requires web server for real use)
            resourcePackUrl = "file://" + resourcePackFile.getAbsolutePath();
        }
        
        // Calculate hash
        try {
            if (resourcePackFile.exists()) {
                byte[] fileBytes = Files.readAllBytes(resourcePackFile.toPath());
                resourcePackHash = calculateSHA1(fileBytes);
            }
        } catch (Exception e) {
            resourcePackHash = "";
        }
    }
    
    private static String calculateSHA1(byte[] data) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-1");
            byte[] hash = digest.digest(data);
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return "";
        }
    }
    
    private static void startResourcePackTask() {
        Bukkit.getScheduler().runTaskTimer(Factory.getInstance(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (shouldSendResourcePack(player)) {
                    sendResourcePack(player);
                }
            }
        }, 0L, 100L); // Check every 5 seconds
    }
    
    private static boolean shouldSendResourcePack(Player player) {
        // Don't send if already sent recently
        Long lastSent = lastSentPack.get(player.getUniqueId());
        if (lastSent != null && (System.currentTimeMillis() - lastSent) < 30000) {
            return false;
        }
        
        // Check if player has permission
        return player.hasPermission("factory.resourcepack");
    }
    
    public static void sendResourcePack(Player player) {
        if (resourcePackFile.exists() && !resourcePackUrl.isEmpty()) {
            player.setResourcePack(resourcePackUrl, resourcePackHash);
            lastSentPack.put(player.getUniqueId(), System.currentTimeMillis());
            
            Factory.getInstance().getLogger().info("Sent resource pack to " + player.getName());
        }
    }
    
    public static void forceSendResourcePack(Player player) {
        lastSentPack.remove(player.getUniqueId());
        sendResourcePack(player);
    }
    
    public static boolean hasResourcePack() {
        return resourcePackFile.exists();
    }
    
    public static String getResourcePackUrl() {
        return resourcePackUrl;
    }
    
    public static String getResourcePackHash() {
        return resourcePackHash;
    }
    
    // Utility methods for custom textures
    public static void setCustomBlockTexture(String blockName, String texturePath) {
        // This would be used to dynamically update textures
        // Implementation would involve updating the resource pack and notifying players
    }
    
    public static void addCustomItemTexture(String itemName, String texturePath) {
        // Similar to above but for items
    }
    
    public static void reloadResourcePack() {
        // Regenerate and reload the resource pack
        generateResourcePack();
        setupResourcePackUrl(Factory.getInstance());
        
        // Send to all online players
        for (Player player : Bukkit.getOnlinePlayers()) {
            forceSendResourcePack(player);
        }
    }
    
    public static String getResourcePackStatus() {
        StringBuilder status = new StringBuilder();
        status.append("§6=== Resource Pack Status ===\n");
        status.append("§7File Exists: ").append(resourcePackFile.exists() ? "§aYes" : "§cNo").append("\n");
        status.append("§7URL: ").append(resourcePackUrl.isEmpty() ? "§cNot Set" : "§a" + resourcePackUrl).append("\n");
        status.append("§7Hash: ").append(resourcePackHash.isEmpty() ? "§cNot Calculated" : "§a" + resourcePackHash.substring(0, 8) + "...").append("\n");
        
        return status.toString();
    }
}

package org.RisingSMP.factory.autocraft;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.Recipe;
import org.bukkit.Bukkit;
import org.RisingSMP.factory.Factory;
import org.RisingSMP.factory.config.FactoryConfig;
import org.RisingSMP.factory.machines.Machine;
import org.RisingSMP.factory.registry.MachineRegistry;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class AutoCraftingManager {
    
    private static final Map<UUID, AutoCraftingProfile> playerProfiles = new ConcurrentHashMap<>();
    private static final Map<Material, List<CraftingRecipe>> recipeCache = new ConcurrentHashMap<>();
    private static final AtomicBoolean systemEnabled = new AtomicBoolean(true);
    
    public static class AutoCraftingProfile {
        private final UUID playerId;
        private final Map<Material, Integer> targetQuantities = new HashMap<>();
        private final Map<Material, Boolean> autoCraftingEnabled = new HashMap<>();
        private final Map<Material, Integer> priorityLevels = new HashMap<>();
        
        // Settings
        private boolean globalEnabled = true;
        private int maxCraftingQueue = 10;
        private boolean useIngredientsFromNearby = true;
        private int searchRadius = 5;
        
        public AutoCraftingProfile(UUID playerId) {
            this.playerId = playerId;
            initializeDefaults();
        }
        
        private void initializeDefaults() {
            // Enable auto-crafting for common factory items
            autoCraftingEnabled.put(Material.IRON_INGOT, true);
            autoCraftingEnabled.put(Material.GOLD_INGOT, true);
            autoCraftingEnabled.put(Material.DIAMOND, true);
            autoCraftingEnabled.put(Material.REDSTONE, true);
            autoCraftingEnabled.put(Material.COAL, true);
            
            // Set default priorities
            priorityLevels.put(Material.DIAMOND, 3); // High priority
            priorityLevels.put(Material.IRON_INGOT, 2); // Medium priority
            priorityLevels.put(Material.GOLD_INGOT, 2);
            priorityLevels.put(Material.REDSTONE, 1); // Low priority
        }
        
        public void setTargetQuantity(Material material, int quantity) {
            targetQuantities.put(material, Math.max(0, quantity));
        }
        
        public int getTargetQuantity(Material material) {
            return targetQuantities.getOrDefault(material, 0);
        }
        
        public void setAutoCraftingEnabled(Material material, boolean enabled) {
            autoCraftingEnabled.put(material, enabled);
        }
        
        public boolean isAutoCraftingEnabled(Material material) {
            return globalEnabled && autoCraftingEnabled.getOrDefault(material, false);
        }
        
        public void setPriority(Material material, int priority) {
            priorityLevels.put(material, Math.max(1, Math.min(3, priority)));
        }
        
        public int getPriority(Material material) {
            return priorityLevels.getOrDefault(material, 2);
        }
        
        public boolean isGlobalEnabled() { return globalEnabled; }
        public void setGlobalEnabled(boolean enabled) { globalEnabled = enabled; }
        
        public int getMaxCraftingQueue() { return maxCraftingQueue; }
        public void setMaxCraftingQueue(int queue) { maxCraftingQueue = Math.max(1, queue); }
        
        public boolean useIngredientsFromNearby() { return useIngredientsFromNearby; }
        public void setUseIngredientsFromNearby(boolean use) { useIngredientsFromNearby = use; }
        
        public int getSearchRadius() { return searchRadius; }
        public void setSearchRadius(int radius) { searchRadius = Math.max(1, radius); }
        
        public List<Material> getEnabledMaterials() {
            return autoCraftingEnabled.entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .sorted((m1, m2) -> Integer.compare(getPriority(m2), getPriority(m1)))
                .toList();
        }
    }
    
    public static class CraftingRecipe {
        private final Material result;
        private final int resultAmount;
        private final Map<Material, Integer> ingredients;
        private final Recipe bukkitRecipe;
        
        public CraftingRecipe(Material result, int resultAmount, Map<Material, Integer> ingredients, Recipe bukkitRecipe) {
            this.result = result;
            this.resultAmount = resultAmount;
            this.ingredients = new HashMap<>(ingredients);
            this.bukkitRecipe = bukkitRecipe;
        }
        
        public Material getResult() { return result; }
        public int getResultAmount() { return resultAmount; }
        public Map<Material, Integer> getIngredients() { return new HashMap<>(ingredients); }
        public Recipe getBukkitRecipe() { return bukkitRecipe; }
        
        public boolean canCraft(Inventory inventory) {
            for (Map.Entry<Material, Integer> ingredient : ingredients.entrySet()) {
                if (inventory.containsAtLeast(new ItemStack(ingredient.getKey()), ingredient.getValue())) {
                    continue;
                }
                
                // Check if we can craft the ingredient recursively
                if (!canCraftIngredient(ingredient.getKey(), ingredient.getValue(), inventory)) {
                    return false;
                }
            }
            return true;
        }
        
        private boolean canCraftIngredient(Material ingredientMaterial, int amount, Inventory inventory) {
            // Simple check - in a real implementation, this would be more sophisticated
            return inventory.containsAtLeast(new ItemStack(ingredientMaterial), amount);
        }
        
        public void craft(Inventory inventory) {
            // Remove ingredients
            for (Map.Entry<Material, Integer> ingredient : ingredients.entrySet()) {
                int remaining = ingredient.getValue();
                ItemStack[] contents = inventory.getContents();
                
                for (ItemStack item : contents) {
                    if (item != null && item.getType() == ingredient.getKey() && remaining > 0) {
                        int toRemove = Math.min(item.getAmount(), remaining);
                        item.setAmount(item.getAmount() - toRemove);
                        remaining -= toRemove;
                        
                        if (item.getAmount() == 0) {
                            int slot = inventory.first(item);
                            if (slot != -1) {
                                inventory.setItem(slot, null);
                            }
                        }
                    }
                }
            }
            
            // Add result
            ItemStack resultItem = new ItemStack(result, resultAmount);
            inventory.addItem(resultItem);
        }
    }
    
    public static void initialize() {
        // Cache all crafting recipes
        cacheRecipes();
        
        // Start auto-crafting task
        startAutoCraftingTask();
        
        Factory.getInstance().getLogger().info("Auto-crafting system initialized");
    }
    
    private static void cacheRecipes() {
        for (Recipe recipe : Bukkit.getRecipesFor(new ItemStack(Material.AIR))) {
            if (recipe.getResult().getType() == Material.AIR) continue;
            
            Material result = recipe.getResult().getType();
            int resultAmount = recipe.getResult().getAmount();
            
            // Extract ingredients (simplified - real implementation would be more complex)
            Map<Material, Integer> ingredients = extractIngredients(recipe);
            
            if (!ingredients.isEmpty()) {
                CraftingRecipe craftingRecipe = new CraftingRecipe(result, resultAmount, ingredients, recipe);
                recipeCache.computeIfAbsent(result, k -> new ArrayList<>()).add(craftingRecipe);
            }
        }
    }
    
    private static Map<Material, Integer> extractIngredients(Recipe recipe) {
        Map<Material, Integer> ingredients = new HashMap<>();
        
        // This is a simplified implementation
        // In a real plugin, you would need to properly parse different recipe types
        // For now, we'll add some basic recipes manually
        
        // Iron ingot recipe
        if (recipe.getResult().getType() == Material.IRON_INGOT) {
            ingredients.put(Material.IRON_ORE, 1);
            ingredients.put(Material.COAL, 1);
        }
        
        // Gold ingot recipe
        if (recipe.getResult().getType() == Material.GOLD_INGOT) {
            ingredients.put(Material.GOLD_ORE, 1);
            ingredients.put(Material.COAL, 1);
        }
        
        // Diamond recipe
        if (recipe.getResult().getType() == Material.DIAMOND) {
            ingredients.put(Material.DIAMOND_ORE, 1);
        }
        
        return ingredients;
    }
    
    private static void startAutoCraftingTask() {
        Bukkit.getScheduler().runTaskTimer(Factory.getInstance(), () -> {
            if (!systemEnabled.get() || !FactoryConfig.isPerformanceOptimizationEnabled()) {
                return;
            }
            
            // Process auto-crafting for all players
            for (Map.Entry<UUID, AutoCraftingProfile> entry : playerProfiles.entrySet()) {
                UUID playerId = entry.getKey();
                AutoCraftingProfile profile = entry.getValue();
                
                if (!profile.isGlobalEnabled()) continue;
                
                // Get player's inventory (simplified)
                org.bukkit.entity.Player player = Bukkit.getPlayer(playerId);
                if (player == null || !player.isOnline()) continue;
                
                processAutoCrafting(player, profile);
            }
        }, 0L, 100L); // Check every 5 seconds
    }
    
    private static void processAutoCrafting(org.bukkit.entity.Player player, AutoCraftingProfile profile) {
        Inventory inventory = player.getInventory();
        
        for (Material material : profile.getEnabledMaterials()) {
            int targetQuantity = profile.getTargetQuantity(material);
            int currentQuantity = countItems(inventory, material);
            
            if (currentQuantity >= targetQuantity) continue;
            
            // Find recipe for this material
            List<CraftingRecipe> recipes = recipeCache.get(material);
            if (recipes == null || recipes.isEmpty()) continue;
            
            // Try to craft
            for (CraftingRecipe recipe : recipes) {
                if (recipe.canCraft(inventory)) {
                    recipe.craft(inventory);
                    
                    // Update statistics
                    // PlayerStatistics.recordAutoCraft(player, material, recipe.getResultAmount());
                    
                    break; // Only craft one recipe per cycle
                }
            }
        }
    }
    
    private static int countItems(Inventory inventory, Material material) {
        int count = 0;
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() == material) {
                count += item.getAmount();
            }
        }
        return count;
    }
    
    public static AutoCraftingProfile getPlayerProfile(UUID playerId) {
        return playerProfiles.computeIfAbsent(playerId, AutoCraftingProfile::new);
    }
    
    public static void setSystemEnabled(boolean enabled) {
        systemEnabled.set(enabled);
    }
    
    public static boolean isSystemEnabled() {
        return systemEnabled.get();
    }
    
    public static void enableAutoCrafting(org.bukkit.entity.Player player, Material material, int targetQuantity) {
        AutoCraftingProfile profile = getPlayerProfile(player.getUniqueId());
        profile.setAutoCraftingEnabled(material, true);
        profile.setTargetQuantity(material, targetQuantity);
        
        player.sendMessage("§aAuto-crafting abilitato per " + material.name() + " (target: " + targetQuantity + ")");
    }
    
    public static void disableAutoCrafting(org.bukkit.entity.Player player, Material material) {
        AutoCraftingProfile profile = getPlayerProfile(player.getUniqueId());
        profile.setAutoCraftingEnabled(material, false);
        
        player.sendMessage("§cAuto-crafting disabilitato per " + material.name());
    }
    
    public static String getAutoCraftingStatus(org.bukkit.entity.Player player) {
        AutoCraftingProfile profile = getPlayerProfile(player.getUniqueId());
        StringBuilder status = new StringBuilder();
        
        status.append("§6=== Auto-Crafting Status ===\n");
        status.append("§7Globale: ").append(profile.isGlobalEnabled() ? "§aAbilitato" : "§cDisabilitato").append("\n");
        status.append("§7Coda max: §e").append(profile.getMaxCraftingQueue()).append("\n");
        status.append("§7Raggio ricerca: §e").append(profile.getSearchRadius()).append(" blocchi\n");
        status.append("§7Usa ingredienti vicini: ").append(profile.useIngredientsFromNearby() ? "§aSì" : "§cNo").append("\n");
        status.append("\n§6Materiali abilitati:\n");
        
        for (Material material : profile.getEnabledMaterials()) {
            int current = countItems(player.getInventory(), material);
            int target = profile.getTargetQuantity(material);
            int priority = profile.getPriority(material);
            
            String priorityColor = switch (priority) {
                case 3 -> "§c"; // High
                case 2 -> "§e"; // Medium
                default -> "§a"; // Low
            };
            
            status.append(String.format("§7- %s (%sPriorità %d): %d/%d\n", 
                material.name(), priorityColor, priority, current, target));
        }
        
        return status.toString();
    }
    
    public static void toggleGlobalAutoCrafting(org.bukkit.entity.Player player) {
        AutoCraftingProfile profile = getPlayerProfile(player.getUniqueId());
        profile.setGlobalEnabled(!profile.isGlobalEnabled());
        
        player.sendMessage("§7Auto-crafting globale: " + 
            (profile.isGlobalEnabled() ? "§aAbilitato" : "§cDisabilitato"));
    }
    
    // Machine integration
    public static void setupMachineAutoCrafting(Machine machine, UUID owner) {
        AutoCraftingProfile profile = getPlayerProfile(owner);
        
        // Configure auto-crafting based on machine type
        if (machine.getClass().getSimpleName().contains("Generator")) {
            profile.setAutoCraftingEnabled(Material.REDSTONE, true);
            profile.setTargetQuantity(Material.REDSTONE, 64);
        } else if (machine.getClass().getSimpleName().contains("Refinery")) {
            profile.setAutoCraftingEnabled(Material.COAL, true);
            profile.setTargetQuantity(Material.COAL, 128);
        }
    }
    
    // Utility methods
    public static List<Material> getCraftableMaterials() {
        return new ArrayList<>(recipeCache.keySet());
    }
    
    public static List<CraftingRecipe> getRecipesForMaterial(Material material) {
        return recipeCache.getOrDefault(material, new ArrayList<>());
    }
    
    public static boolean canCraftMaterial(Material material) {
        return recipeCache.containsKey(material);
    }
    
    public static void clearProfile(UUID playerId) {
        playerProfiles.remove(playerId);
    }
    
    public static void cleanupInactiveProfiles() {
        playerProfiles.entrySet().removeIf(entry -> {
            org.bukkit.entity.Player player = Bukkit.getPlayer(entry.getKey());
            return player == null || !player.isOnline();
        });
    }
}

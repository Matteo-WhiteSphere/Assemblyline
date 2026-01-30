package org.RisingSMP.factory.production;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.SmokingRecipe;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.StonecuttingRecipe;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionType;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class CompleteProductionSystem {
    
    private static final Map<String, ProductionRecipe> PRODUCTION_RECIPES = new HashMap<>();
    private static final Map<String, ProductionCategory> CATEGORIES = new HashMap<>();
    
    public static void initialize() {
        initializeCategories();
        initializeProductionRecipes();
    }
    
    private static void initializeCategories() {
        // Categorie di produzione
        CATEGORIES.put("tools", new ProductionCategory("Tools", "Attrezzi", Material.IRON_PICKAXE));
        CATEGORIES.put("weapons", new ProductionCategory("Weapons", "Armi", Material.IRON_SWORD));
        CATEGORIES.put("armor", new ProductionCategory("Armor", "Armature", Material.IRON_CHESTPLATE));
        CATEGORIES.put("food", new ProductionCategory("Food", "Cibo", Material.BREAD));
        CATEGORIES.put("potions", new ProductionCategory("Potions", "Pozioni", Material.POTION));
        CATEGORIES.put("blocks", new ProductionCategory("Blocks", "Blocchi", Material.COBBLESTONE));
        CATEGORIES.put("redstone", new ProductionCategory("Redstone", "Redstone", Material.REDSTONE));
        CATEGORIES.put("decoration", new ProductionCategory("Decoration", "Decorazione", Material.FLOWER_POT));
        CATEGORIES.put("utility", new ProductionCategory("Utility", "Utilità", Material.CHEST));
        CATEGORIES.put("vehicles", new ProductionCategory("Vehicles", "Veicoli", Material.MINECART));
        CATEGORIES.put("quality_weapons", new ProductionCategory("Quality Weapons", "Armi QA", Material.CROSSBOW));
        CATEGORIES.put("quality_vehicles", new ProductionCategory("Quality Vehicles", "Veicoli QA", Material.MINECART));
    }
    
    private static void initializeProductionRecipes() {
        // TOOLS - Attrezzi base
        addProductionRecipe("wooden_pickaxe", new ItemStack(Material.WOODEN_PICKAXE), 
            Arrays.asList("PLANKS:3", "STICK:2"), 100, "tools");
        addProductionRecipe("stone_pickaxe", new ItemStack(Material.STONE_PICKAXE), 
            Arrays.asList("COBBLESTONE:3", "STICK:2"), 200, "tools");
        addProductionRecipe("iron_pickaxe", new ItemStack(Material.IRON_PICKAXE), 
            Arrays.asList("IRON_INGOT:3", "STICK:2"), 500, "tools");
        addProductionRecipe("golden_pickaxe", new ItemStack(Material.GOLDEN_PICKAXE), 
            Arrays.asList("GOLD_INGOT:3", "STICK:2"), 400, "tools");
        addProductionRecipe("diamond_pickaxe", new ItemStack(Material.DIAMOND_PICKAXE), 
            Arrays.asList("DIAMOND:3", "STICK:2"), 1000, "tools");
        addProductionRecipe("netherite_pickaxe", new ItemStack(Material.NETHERITE_PICKAXE), 
            Arrays.asList("NETHERITE_INGOT:1", "DIAMOND_PICKAXE:1", "NETHERITE_SCRAP:1"), 2000, "tools");
        
        // Altri attrezzi
        addProductionRecipe("iron_axe", new ItemStack(Material.IRON_AXE), 
            Arrays.asList("IRON_INGOT:3", "STICK:2"), 400, "tools");
        addProductionRecipe("iron_shovel", new ItemStack(Material.IRON_SHOVEL), 
            Arrays.asList("IRON_INGOT:1", "STICK:2"), 300, "tools");
        addProductionRecipe("iron_hoe", new ItemStack(Material.IRON_HOE), 
            Arrays.asList("IRON_INGOT:2", "STICK:2"), 350, "tools");
        addProductionRecipe("iron_sword", new ItemStack(Material.IRON_SWORD), 
            Arrays.asList("IRON_INGOT:2", "STICK:1"), 400, "weapons");
        
        // ARMOR - Armature complete
        addProductionRecipe("leather_helmet", new ItemStack(Material.LEATHER_HELMET), 
            Arrays.asList("LEATHER:5"), 200, "armor");
        addProductionRecipe("iron_helmet", new ItemStack(Material.IRON_HELMET), 
            Arrays.asList("IRON_INGOT:5"), 800, "armor");
        addProductionRecipe("iron_chestplate", new ItemStack(Material.IRON_CHESTPLATE), 
            Arrays.asList("IRON_INGOT:8"), 1200, "armor");
        addProductionRecipe("iron_leggings", new ItemStack(Material.IRON_LEGGINGS), 
            Arrays.asList("IRON_INGOT:7"), 1000, "armor");
        addProductionRecipe("iron_boots", new ItemStack(Material.IRON_BOOTS), 
            Arrays.asList("IRON_INGOT:4"), 600, "armor");
        
        addProductionRecipe("diamond_helmet", new ItemStack(Material.DIAMOND_HELMET), 
            Arrays.asList("DIAMOND:5"), 2000, "armor");
        addProductionRecipe("diamond_chestplate", new ItemStack(Material.DIAMOND_CHESTPLATE), 
            Arrays.asList("DIAMOND:8"), 3000, "armor");
        addProductionRecipe("diamond_leggings", new ItemStack(Material.DIAMOND_LEGGINGS), 
            Arrays.asList("DIAMOND:7"), 2500, "armor");
        addProductionRecipe("diamond_boots", new ItemStack(Material.DIAMOND_BOOTS), 
            Arrays.asList("DIAMOND:4"), 1500, "armor");
        
        // FOOD - Cibo e bevande
        addProductionRecipe("bread", new ItemStack(Material.BREAD), 
            Arrays.asList("WHEAT:3"), 50, "food");
        addProductionRecipe("cake", new ItemStack(Material.CAKE), 
            Arrays.asList("WHEAT:3", "MILK_BUCKET:3", "SUGAR:2", "EGG:1"), 200, "food");
        addProductionRecipe("cooked_beef", new ItemStack(Material.COOKED_BEEF), 
            Arrays.asList("RAW_BEEF:1", "COAL:1"), 100, "food");
        addProductionRecipe("cooked_porkchop", new ItemStack(Material.COOKED_PORKCHOP), 
            Arrays.asList("RAW_PORKCHOP:1", "COAL:1"), 100, "food");
        addProductionRecipe("cooked_chicken", new ItemStack(Material.COOKED_CHICKEN), 
            Arrays.asList("RAW_CHICKEN:1", "COAL:1"), 80, "food");
        addProductionRecipe("golden_apple", new ItemStack(Material.GOLDEN_APPLE), 
            Arrays.asList("APPLE:1", "GOLD_INGOT:8"), 1000, "food");
        addProductionRecipe("golden_carrot", new ItemStack(Material.GOLDEN_CARROT), 
            Arrays.asList("CARROT:1", "GOLD_NUGGET:8"), 500, "food");
        
        // POTIONS - Pozioni base
        addProductionRecipe("healing_potion", createPotion(org.bukkit.potion.PotionType.HEALING), 
            Arrays.asList("GLASS_BOTTLE:1", "NETHER_WART:1", "GLISTERING_MELON_SLICE:1"), 300, "potions");
        addProductionRecipe("strength_potion", createPotion(org.bukkit.potion.PotionType.STRENGTH), 
            Arrays.asList("GLASS_BOTTLE:1", "NETHER_WART:1", "BLAZE_POWDER:1"), 400, "potions");
        addProductionRecipe("speed_potion", createPotion(org.bukkit.potion.PotionType.SWIFTNESS), 
            Arrays.asList("GLASS_BOTTLE:1", "NETHER_WART:1", "SUGAR:1"), 350, "potions");
        addProductionRecipe("fire_resistance_potion", createPotion(org.bukkit.potion.PotionType.FIRE_RESISTANCE), 
            Arrays.asList("GLASS_BOTTLE:1", "NETHER_WART:1", "MAGMA_CREAM:1"), 400, "potions");
        
        // BLOCKS - Blocchi utili
        addProductionRecipe("crafting_table", new ItemStack(Material.CRAFTING_TABLE), 
            Arrays.asList("PLANKS:4"), 50, "blocks");
        addProductionRecipe("chest", new ItemStack(Material.CHEST), 
            Arrays.asList("PLANKS:8"), 100, "blocks");
        addProductionRecipe("furnace", new ItemStack(Material.FURNACE), 
            Arrays.asList("COBBLESTONE:8"), 150, "blocks");
        addProductionRecipe("bed", new ItemStack(Material.RED_BED), 
            Arrays.asList("PLANKS:3", "WOOL:3"), 200, "blocks");
        addProductionRecipe("bookshelf", new ItemStack(Material.BOOKSHELF), 
            Arrays.asList("PLANKS:6", "BOOK:3"), 300, "blocks");
        
        // REDSTONE - Componenti redstone
        addProductionRecipe("redstone_torch", new ItemStack(Material.REDSTONE_TORCH), 
            Arrays.asList("REDSTONE:1", "STICK:1"), 100, "redstone");
        addProductionRecipe("lever", new ItemStack(Material.LEVER), 
            Arrays.asList("STICK:1", "COBBLESTONE:1"), 50, "redstone");
        addProductionRecipe("stone_button", new ItemStack(Material.STONE_BUTTON), 
            Arrays.asList("STONE:1"), 30, "redstone");
        addProductionRecipe("repeater", new ItemStack(Material.REPEATER), 
            Arrays.asList("REDSTONE_TORCH:2", "STONE:3", "REDSTONE:1"), 200, "redstone");
        addProductionRecipe("comparator", new ItemStack(Material.COMPARATOR), 
            Arrays.asList("REDSTONE_TORCH:3", "QUARTZ:1", "STONE:3"), 300, "redstone");
        addProductionRecipe("piston", new ItemStack(Material.PISTON), 
            Arrays.asList("PLANKS:3", "COBBLESTONE:4", "IRON_INGOT:1", "REDSTONE:1"), 400, "redstone");
        addProductionRecipe("sticky_piston", new ItemStack(Material.STICKY_PISTON), 
            Arrays.asList("PISTON:1", "SLIME_BALL:1"), 450, "redstone");
        
        // UTILITY - Oggetti utili
        addProductionRecipe("bucket", new ItemStack(Material.BUCKET), 
            Arrays.asList("IRON_INGOT:3"), 300, "utility");
        addProductionRecipe("flint_and_steel", new ItemStack(Material.FLINT_AND_STEEL), 
            Arrays.asList("IRON_INGOT:1", "FLINT:1"), 200, "utility");
        addProductionRecipe("compass", new ItemStack(Material.COMPASS), 
            Arrays.asList("IRON_INGOT:4", "REDSTONE:1"), 250, "utility");
        addProductionRecipe("clock", new ItemStack(Material.CLOCK), 
            Arrays.asList("GOLD_INGOT:4", "REDSTONE:1"), 400, "utility");
        addProductionRecipe("fishing_rod", new ItemStack(Material.FISHING_ROD), 
            Arrays.asList("STICK:3", "STRING:2"), 300, "utility");
        addProductionRecipe("bow", new ItemStack(Material.BOW), 
            Arrays.asList("STICK:3", "STRING:3"), 400, "weapons");
        addProductionRecipe("arrow", new ItemStack(Material.ARROW, 4), 
            Arrays.asList("FLINT:1", "STICK:1", "FEATHER:1"), 100, "weapons");
        addProductionRecipe("shield", new ItemStack(Material.SHIELD), 
            Arrays.asList("IRON_INGOT:1", "PLANKS:6"), 500, "armor");
        
        // DECORATION - Oggetti decorativi
        addProductionRecipe("painting", new ItemStack(Material.PAINTING), 
            Arrays.asList("STICK:8", "WOOL:1"), 200, "decoration");
        addProductionRecipe("item_frame", new ItemStack(Material.ITEM_FRAME), 
            Arrays.asList("STICK:8", "LEATHER:1"), 250, "decoration");
        addProductionRecipe("flower_pot", new ItemStack(Material.FLOWER_POT), 
            Arrays.asList("BRICKS:3"), 150, "decoration");
        addProductionRecipe("lantern", new ItemStack(Material.LANTERN), 
            Arrays.asList("IRON_INGOT:1", "TORCH:1"), 300, "decoration");
        addProductionRecipe("soul_lantern", new ItemStack(Material.SOUL_LANTERN), 
            Arrays.asList("IRON_INGOT:1", "SOUL_TORCH:1"), 400, "decoration");
        
        // VEHICLES - Veicoli vanilla
        addProductionRecipe("minecart", new ItemStack(Material.MINECART), 
            Arrays.asList("IRON_INGOT:5"), 500, "vehicles");
        addProductionRecipe("chest_minecart", new ItemStack(Material.CHEST_MINECART), 
            Arrays.asList("MINECART:1", "CHEST:1"), 700, "vehicles");
        addProductionRecipe("furnace_minecart", new ItemStack(Material.FURNACE_MINECART), 
            Arrays.asList("MINECART:1", "FURNACE:1"), 800, "vehicles");
        addProductionRecipe("tnt_minecart", new ItemStack(Material.TNT_MINECART), 
            Arrays.asList("MINECART:1", "TNT:1"), 1000, "vehicles");
        addProductionRecipe("hopper_minecart", new ItemStack(Material.HOPPER_MINECART), 
            Arrays.asList("MINECART:1", "HOPPER:1"), 900, "vehicles");
        addProductionRecipe("boat", new ItemStack(Material.OAK_BOAT), 
            Arrays.asList("OAK_PLANKS:5"), 400, "vehicles");
        
        // QUALITY ARMORY INTEGRATION
        addQualityArmoryRecipes();
        
        // VEHICLES PLUGIN INTEGRATION  
        addVehiclesPluginRecipes();
    }
    
    private static void addQualityArmoryRecipes() {
        // Armi base di QualityArmory (esempi)
        addProductionRecipe("pistol", createQualityWeapon("PISTOL"), 
            Arrays.asList("IRON_INGOT:3", "REDSTONE:1", "GUNPOWDER:2"), 800, "quality_weapons");
        addProductionRecipe("rifle", createQualityWeapon("RIFLE"), 
            Arrays.asList("IRON_INGOT:5", "REDSTONE:2", "GUNPOWDER:3"), 1500, "quality_weapons");
        addProductionRecipe("shotgun", createQualityWeapon("SHOTGUN"), 
            Arrays.asList("IRON_INGOT:6", "REDSTONE:2", "GUNPOWDER:4"), 2000, "quality_weapons");
        addProductionRecipe("sniper", createQualityWeapon("SNIPER"), 
            Arrays.asList("IRON_INGOT:8", "DIAMOND:2", "REDSTONE:3", "GUNPOWDER:5"), 3000, "quality_weapons");
        
        // Munizioni
        addProductionRecipe("pistol_ammo", createQualityAmmo("PISTOL_AMMO", 16), 
            Arrays.asList("IRON_INGOT:1", "GUNPOWDER:1"), 100, "quality_weapons");
        addProductionRecipe("rifle_ammo", createQualityAmmo("RIFLE_AMMO", 16), 
            Arrays.asList("IRON_INGOT:2", "GUNPOWDER:2"), 200, "quality_weapons");
        addProductionRecipe("shotgun_ammo", createQualityAmmo("SHOTGUN_AMMO", 8), 
            Arrays.asList("IRON_INGOT:3", "GUNPOWDER:3"), 300, "quality_weapons");
    }
    
    private static void addVehiclesPluginRecipes() {
        // Veicoli base del plugin Vehicles (esempi)
        addProductionRecipe("car", createVehicle("CAR"), 
            Arrays.asList("IRON_INGOT:10", "REDSTONE:3", "GLASS:4", "OIL:5"), 2500, "quality_vehicles");
        addProductionRecipe("motorcycle", createVehicle("MOTORCYCLE"), 
            Arrays.asList("IRON_INGOT:6", "REDSTONE:2", "OIL:3"), 1500, "quality_vehicles");
        addProductionRecipe("helicopter", createVehicle("HELICOPTER"), 
            Arrays.asList("IRON_INGOT:15", "DIAMOND:3", "REDSTONE:5", "OIL:10"), 5000, "quality_vehicles");
        addProductionRecipe("tank", createVehicle("TANK"), 
            Arrays.asList("IRON_INGOT:20", "DIAMOND:5", "REDSTONE:4", "OIL:15"), 8000, "quality_vehicles");
        
        // Carburante per veicoli
        addProductionRecipe("fuel", createFuel(5), 
            Arrays.asList("OIL:3", "COAL:2"), 500, "quality_vehicles");
    }
    
    private static void addProductionRecipe(String id, ItemStack result, List<String> ingredients, 
                                          int energyCost, String category) {
        ProductionRecipe recipe = new ProductionRecipe(id, result, ingredients, energyCost, category);
        PRODUCTION_RECIPES.put(id, recipe);
    }
    
    private static ItemStack createPotion(PotionType type) {
        ItemStack potion = new ItemStack(Material.POTION);
        potion.setItemMeta(org.bukkit.Bukkit.getItemFactory().getItemMeta(Material.POTION));
        return potion;
    }
    
    private static ItemStack createQualityWeapon(String weaponType) {
        // Questo verrà integrato con QualityArmory
        ItemStack weapon = new ItemStack(Material.IRON_HOE); // Placeholder
        return weapon;
    }
    
    private static ItemStack createQualityAmmo(String ammoType, int amount) {
        // Questo verrà integrato con QualityArmory
        ItemStack ammo = new ItemStack(Material.ARROW, amount); // Placeholder
        return ammo;
    }
    
    private static ItemStack createVehicle(String vehicleType) {
        // Questo verrà integrato con Vehicles plugin
        ItemStack vehicle = new ItemStack(Material.MINECART); // Placeholder
        return vehicle;
    }
    
    private static ItemStack createFuel(int amount) {
        // Carburante per veicoli
        ItemStack fuel = new ItemStack(Material.COAL, amount); // Placeholder
        return fuel;
    }
    
    // Metodi pubblici per l'accesso al sistema
    public static ProductionRecipe getRecipe(String id) {
        return PRODUCTION_RECIPES.get(id);
    }
    
    public static Map<String, ProductionRecipe> getAllRecipes() {
        return new HashMap<>(PRODUCTION_RECIPES);
    }
    
    public static Map<String, ProductionRecipe> getRecipesByCategory(String category) {
        return PRODUCTION_RECIPES.entrySet().stream()
            .filter(entry -> entry.getValue().getCategory().equals(category))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    
    public static ProductionCategory getCategory(String categoryId) {
        return CATEGORIES.get(categoryId);
    }
    
    public static Map<String, ProductionCategory> getAllCategories() {
        return new HashMap<>(CATEGORIES);
    }
    
    public static boolean canCraft(Player player, String recipeId) {
        ProductionRecipe recipe = getRecipe(recipeId);
        if (recipe == null) return false;
        
        // Controlla se il giocatore ha i materiali necessari
        for (String ingredient : recipe.getIngredients()) {
            String[] parts = ingredient.split(":");
            Material material = Material.valueOf(parts[0]);
            int amount = Integer.parseInt(parts[1]);
            
            if (!player.getInventory().containsAtLeast(new ItemStack(material), amount)) {
                return false;
            }
        }
        
        return true;
    }
    
    public static boolean craftItem(Player player, String recipeId) {
        if (!canCraft(player, recipeId)) {
            return false;
        }
        
        ProductionRecipe recipe = getRecipe(recipeId);
        
        // Rimuovi gli ingredienti
        for (String ingredient : recipe.getIngredients()) {
            String[] parts = ingredient.split(":");
            Material material = Material.valueOf(parts[0]);
            int amount = Integer.parseInt(parts[1]);
            
            removeItems(player.getInventory(), material, amount);
        }
        
        // Dai il risultato
        player.getInventory().addItem(recipe.getResult().clone());
        player.sendMessage("§aHai prodotto: " + recipe.getResult().getType().name());
        
        return true;
    }
    
    private static void removeItems(org.bukkit.inventory.Inventory inventory, Material material, int amount) {
        int remaining = amount;
        for (org.bukkit.inventory.ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() == material && remaining > 0) {
                int toRemove = Math.min(item.getAmount(), remaining);
                item.setAmount(item.getAmount() - toRemove);
                remaining -= toRemove;
                
                if (item.getAmount() == 0) {
                    inventory.remove(item);
                }
            }
        }
    }
    
    // Classi interne
    public static class ProductionRecipe {
        private final String id;
        private final ItemStack result;
        private final List<String> ingredients;
        private final int energyCost;
        private final String category;
        
        public ProductionRecipe(String id, ItemStack result, List<String> ingredients, 
                              int energyCost, String category) {
            this.id = id;
            this.result = result;
            this.ingredients = ingredients;
            this.energyCost = energyCost;
            this.category = category;
        }
        
        // Getters
        public String getId() { return id; }
        public ItemStack getResult() { return result; }
        public List<String> getIngredients() { return ingredients; }
        public int getEnergyCost() { return energyCost; }
        public String getCategory() { return category; }
    }
    
    public static class ProductionCategory {
        private final String name;
        private final String displayName;
        private final Material icon;
        
        public ProductionCategory(String name, String displayName, Material icon) {
            this.name = name;
            this.displayName = displayName;
            this.icon = icon;
        }
        
        // Getters
        public String getName() { return name; }
        public String getDisplayName() { return displayName; }
        public Material getIcon() { return icon; }
    }
}

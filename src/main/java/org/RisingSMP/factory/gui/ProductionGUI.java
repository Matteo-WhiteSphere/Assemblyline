package org.RisingSMP.factory.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.ChatColor;

import org.RisingSMP.factory.production.CompleteProductionSystem;
import org.RisingSMP.factory.production.CompleteProductionSystem.ProductionRecipe;
import org.RisingSMP.factory.production.CompleteProductionSystem.ProductionCategory;

import java.util.*;
import java.util.stream.Collectors;

public class ProductionGUI {
    
    private static final String GUI_TITLE = "§6Factory Production System";
    private static final int GUI_SIZE = 54;
    
    private final Map<UUID, String> currentCategory = new HashMap<>();
    private final Map<UUID, Integer> currentPage = new HashMap<>();
    
    public ProductionGUI() {
        // Costruttore
    }
    
    public void openMainMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, GUI_TITLE);
        currentCategory.put(player.getUniqueId(), "main");
        currentPage.put(player.getUniqueId(), 0);
        
        // Bordo decorativo
        ItemStack border = createItem(Material.GRAY_STAINED_GLASS_PANE, "§r", null);
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, border);
            inv.setItem(i + 45, border);
        }
        for (int i = 0; i < 54; i += 9) {
            inv.setItem(i, border);
            inv.setItem(i + 8, border);
        }
        
        // Mostra categorie
        Map<String, ProductionCategory> categories = CompleteProductionSystem.getAllCategories();
        int slot = 10;
        
        for (ProductionCategory category : categories.values()) {
            if (slot >= 35) break; // Limite slot
            
            ItemStack categoryItem = createCategoryItem(category);
            inv.setItem(slot, categoryItem);
            
            slot++;
            if (slot % 9 == 8) slot += 2; // Salta bordi
        }
        
        // Info panel
        ItemStack info = createItem(Material.BOOK, "§6Informazioni Produzione", 
            Arrays.asList(
                "§7Benvenuto nel sistema di produzione!",
                "§7Seleziona una categoria per vedere gli item",
                "§7che puoi produrre con le tue fabbriche.",
                "",
                "§eCosto energia: §fVaria per item",
                "§eMateriali richiesti: §fDipende dall'item",
                "",
                "§aClicca su una categoria per iniziare!"
            ));
        inv.setItem(49, info);
        
        player.openInventory(inv);
    }
    
    public void openCategory(Player player, String categoryId) {
        currentCategory.put(player.getUniqueId(), categoryId);
        currentPage.put(player.getUniqueId(), 0);
        
        ProductionCategory category = CompleteProductionSystem.getCategory(categoryId);
        if (category == null) {
            openMainMenu(player);
            return;
        }
        
        Inventory inv = Bukkit.createInventory(null, 54, "§6" + category.getDisplayName());
        
        // Bordo decorativo
        ItemStack border = createItem(Material.GRAY_STAINED_GLASS_PANE, "§r", null);
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, border);
            inv.setItem(i + 45, border);
        }
        for (int i = 0; i < 54; i += 9) {
            inv.setItem(i, border);
            inv.setItem(i + 8, border);
        }
        
        // Back button
        ItemStack back = createItem(Material.ARROW, "§c← Indietro", 
            Arrays.asList("§7Torna al menu principale"));
        inv.setItem(45, back);
        
        // Mostra ricette della categoria
        Map<String, ProductionRecipe> recipes = CompleteProductionSystem.getRecipesByCategory(categoryId);
        List<ProductionRecipe> recipeList = new ArrayList<>(recipes.values());
        
        int page = currentPage.getOrDefault(player.getUniqueId(), 0);
        int startIndex = page * 28; // 28 slot per pagina (esclusi bordi e back)
        int endIndex = Math.min(startIndex + 28, recipeList.size());
        
        int slot = 10;
        for (int i = startIndex; i < endIndex; i++) {
            if (slot >= 35) {
                slot = 10;
                // Passa alla prossima pagina se necessario
                break;
            }
            
            ProductionRecipe recipe = recipeList.get(i);
            ItemStack recipeItem = createRecipeItem(recipe);
            inv.setItem(slot, recipeItem);
            
            slot++;
            if (slot % 9 == 8) slot += 2; // Salta bordi
        }
        
        // Navigazione pagine
        if (page > 0) {
            ItemStack prevPage = createItem(Material.PAPER, "§e← Pagina Precedente", 
                Arrays.asList("§7Vai alla pagina " + page));
            inv.setItem(48, prevPage);
        }
        
        if (endIndex < recipeList.size()) {
            ItemStack nextPage = createItem(Material.PAPER, "§ePagina Successiva →", 
                Arrays.asList("§7Vai alla pagina " + (page + 2)));
            inv.setItem(50, nextPage);
        }
        
        player.openInventory(inv);
    }
    
    private ItemStack createCategoryItem(ProductionCategory category) {
        ItemStack item = new ItemStack(category.getIcon());
        ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName("§6" + category.getDisplayName());
        
        Map<String, ProductionRecipe> recipes = CompleteProductionSystem.getRecipesByCategory(category.getName());
        List<String> lore = Arrays.asList(
            "§7Clicca per vedere gli item producibili",
            "",
            "§eItem disponibili: §f" + recipes.size(),
            "§7Clicca per esplorare questa categoria"
        );
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }
    
    private ItemStack createRecipeItem(ProductionRecipe recipe) {
        ItemStack item = recipe.getResult().clone();
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            String displayName = meta.hasDisplayName() ? meta.getDisplayName() : 
                "§f" + formatMaterialName(item.getType().name());
            meta.setDisplayName(displayName);
            
            List<String> lore = new ArrayList<>();
            lore.add("§7Costo Energia: §e" + recipe.getEnergyCost());
            lore.add("");
            lore.add("§6Materiali Richiesti:");
            
            for (String ingredient : recipe.getIngredients()) {
                String[] parts = ingredient.split(":");
                String material = formatMaterialName(parts[0]);
                int amount = Integer.parseInt(parts[1]);
                lore.add("§7- §f" + amount + "x " + material);
            }
            
            lore.add("");
            lore.add("§aClicca per produrre!");
            
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    private ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(name);
            if (lore != null) {
                meta.setLore(lore);
            }
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    private String formatMaterialName(String materialName) {
        return Arrays.stream(materialName.toLowerCase().split("_"))
            .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
            .collect(Collectors.joining(" "));
    }
    
    public void handleClick(Player player, ItemStack clicked, int slot) {
        if (clicked == null || !clicked.hasItemMeta()) return;
        
        String displayName = clicked.getItemMeta().getDisplayName();
        
        // Back button
        if (displayName.equals("§c← Indietro")) {
            openMainMenu(player);
            return;
        }
        
        // Navigation buttons
        if (displayName.contains("Pagina")) {
            String categoryId = currentCategory.get(player.getUniqueId());
            if (categoryId != null && !categoryId.equals("main")) {
                int page = currentPage.getOrDefault(player.getUniqueId(), 0);
                
                if (displayName.contains("Precedente")) {
                    currentPage.put(player.getUniqueId(), page - 1);
                } else if (displayName.contains("Successiva")) {
                    currentPage.put(player.getUniqueId(), page + 1);
                }
                
                openCategory(player, categoryId);
            }
            return;
        }
        
        // Category selection
        if (displayName.startsWith("§6") && !displayName.contains("←") && !displayName.contains("Pagina")) {
            String categoryName = ChatColor.stripColor(displayName);
            
            // Trova la categoria corrispondente
            for (ProductionCategory category : CompleteProductionSystem.getAllCategories().values()) {
                if (category.getDisplayName().equals(categoryName)) {
                    openCategory(player, category.getName());
                    return;
                }
            }
        }
        
        // Recipe crafting
        if (displayName.startsWith("§f") || displayName.startsWith("§6")) {
            // Prova a produrre l'item
            String categoryId = currentCategory.get(player.getUniqueId());
            if (categoryId != null && !categoryId.equals("main")) {
                Map<String, ProductionRecipe> recipes = CompleteProductionSystem.getRecipesByCategory(categoryId);
                
                // Trova la ricetta corrispondente
                for (ProductionRecipe recipe : recipes.values()) {
                    ItemStack result = recipe.getResult();
                    String recipeName = result.getItemMeta() != null && result.getItemMeta().hasDisplayName() ?
                        result.getItemMeta().getDisplayName() : formatMaterialName(result.getType().name());
                    
                    if (recipeName.equals(ChatColor.stripColor(displayName))) {
                        // Prova a craftare
                        if (CompleteProductionSystem.canCraft(player, recipe.getId())) {
                            if (CompleteProductionSystem.craftItem(player, recipe.getId())) {
                                player.sendMessage("§a✓ Item prodotto con successo!");
                                
                                // Ricarica la GUI per aggiornare stato
                                openCategory(player, categoryId);
                            } else {
                                player.sendMessage("§c✗ Errore durante la produzione!");
                            }
                        } else {
                            player.sendMessage("§c✗ Non hai i materiali necessari!");
                        }
                        return;
                    }
                }
            }
        }
    }
    
    public Map<UUID, String> getCurrentCategory() {
        return currentCategory;
    }
    
    public Map<UUID, Integer> getCurrentPage() {
        return currentPage;
    }
}

package org.RisingSMP.factory.gui;

import org.RisingSMP.factory.Factory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.RisingSMP.factory.energy.EnergyManager;

import java.util.HashMap;
import java.util.Map;

public class FactoryGUI implements Listener {

    private final Factory plugin;

    // mappa blocco -> ricetta: input -> output
    public static final Map<String, Map<Material, Material>> factoryRecipes = new HashMap<>();

    // mappa blocco FINAL -> arma scelta
    public static final Map<String, String> factoryWeaponsType = new HashMap<>();
    
    // mappa blocco FINAL -> ItemStack dell'arma
    public static final Map<String, ItemStack> factoryWeapons = new HashMap<>();

    public FactoryGUI(Factory plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    // Apri GUI
    public void openGUI(Player player, String factoryType) {
        Inventory inv = Bukkit.createInventory(null, 9, "Configura " + factoryType);

        // Blocchi intermedia/vanilla
        if (!factoryType.equals("FINAL")) {
            inv.setItem(0, createItem(Material.IRON_INGOT, "Input Ferro -> Output"));
            inv.setItem(1, createItem(Material.GOLD_INGOT, "Input Oro -> Output"));
            inv.setItem(2, createItem(Material.DIAMOND, "Input Diamante -> Output"));
        } else {
            // FINALMachine: scelta arma
            inv.setItem(0, createItem(Material.DIAMOND_SWORD, "AK47"));
            inv.setItem(1, createItem(Material.IRON_SWORD, "M4A1"));
            inv.setItem(2, createItem(Material.GOLDEN_SWORD, "Deagle"));
        }

        // salva il tipo di fabbrica nell'inventory per click handler
        inv.setItem(8, createItem(Material.PAPER, factoryType));

        player.openInventory(inv);
    }

    private ItemStack createItem(Material mat, String name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        Inventory inv = e.getInventory();
        if (!e.getView().getTitle().startsWith("Configura")) return;

        e.setCancelled(true);

        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        // recupera il tipo della fabbrica
        String factoryType = inv.getItem(8).getItemMeta().getDisplayName();

        if (factoryType.equals("FINAL")) {
            String weaponName = clicked.getItemMeta().getDisplayName();
            factoryWeaponsType.put(factoryType, weaponName);
            factoryWeapons.put(factoryType, clicked.clone());
            player.sendMessage("§aFinalMachine ora produrrà: " + weaponName);
        } else {
            // esempio: salva input->output semplice
            Map<Material, Material> recipes = factoryRecipes.getOrDefault(factoryType, new HashMap<>());
            Material input = Material.IRON_INGOT; // per esempio si può scegliere input più tardi
            Material output = clicked.getType();
            recipes.put(input, output);
            factoryRecipes.put(factoryType, recipes);
            player.sendMessage("§aHai settato la ricetta della fabbrica " + factoryType + ": " + input + " -> " + output);
        }
    }
}
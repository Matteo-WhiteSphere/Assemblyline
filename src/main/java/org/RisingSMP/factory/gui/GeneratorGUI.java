package org.RisingSMP.factory.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.RisingSMP.factory.machines.GeneratorMachine;

import java.util.ArrayList;
import java.util.List;

public class GeneratorGUI {

    private final Inventory inventory;
    private final GeneratorMachine generator;

    public GeneratorGUI(GeneratorMachine generator) {
        this.generator = generator;
        inventory = Bukkit.createInventory(null, 27, "Generator Control");
        build();
    }
    
    // Costruttore di default per compatibilità
    public GeneratorGUI() {
        this(null);
    }

    private void build() {
        inventory.clear();

        // Stato generatore
        boolean isActive = generator != null && generator.isActive();
        int level = generator != null ? generator.getLevel() : 1;
        int production = generator != null ? generator.getProduction() : 5;
        int range = generator != null ? generator.getRange() : 10;
        
        inventory.setItem(11, createItem(
                Material.REDSTONE,
                "§cStato Generatore",
                "§7Stato: " + (isActive ? "§aATTIVO" : "§cSPENTO"),
                "§7Produzione: §e+" + production + " ⚡"
        ));

        // Upgrade
        int upgradeCost = generator != null ? generator.getUpgradeCost(level) : 10;
        boolean canUpgrade = generator != null && generator.canUpgrade();
        
        inventory.setItem(13, createItem(
                canUpgrade ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK,
                canUpgrade ? "§aUpgrade Generatore" : "§cUpgrade Massimo",
                "§7Livello attuale: §e" + level,
                canUpgrade ? "§7Costo: §a" + upgradeCost + " Emerald Blocks" : "§7Massimo livello raggiunto",
                canUpgrade ? "§7Clicca per aggiornare!" : "§7Non puoi più aggiornare"
        ));

        // Info
        inventory.setItem(15, createItem(
                Material.PAPER,
                "§eInformazioni",
                "§7Livello: §e" + level,
                "§7Range: §e" + range + " blocchi"
        ));

        // Back
        inventory.setItem(26, createItem(
                Material.BARRIER,
                "§cIndietro"
        ));
    }

    private ItemStack createItem(Material mat, String name, String... loreLines) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            List<String> lore = new ArrayList<>();
            for (String s : loreLines) lore.add(s);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    public Inventory getInventory() {
        return inventory;
    }
}
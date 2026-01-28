package org.RisingSMP.factory.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.RisingSMP.factory.machines.RefineryMachine;

import java.util.ArrayList;
import java.util.List;

public class RefineryGUI {

    private final Inventory inventory;
    private final RefineryMachine refinery;

    public RefineryGUI(RefineryMachine refinery) {
        this.refinery = refinery;
        inventory = Bukkit.createInventory(null, 27, "Refinery Control");
        build();
    }
    
    // Costruttore di default per compatibilità
    public RefineryGUI() {
        this(null);
    }

    private void build() {
        inventory.clear();

        // Stato raffineria
        int level = refinery != null ? refinery.getLevel() : 1;
        int energyCost = refinery != null ? refinery.getEnergyCost() : 5;
        
        inventory.setItem(11, createItem(
                Material.LAVA_BUCKET,
                "§cStato Raffineria",
                "§7Stato: §aATTIVA",
                "§7Consumo: §e-" + energyCost + " ⚡"
        ));

        // Upgrade
        inventory.setItem(13, createItem(
                Material.EMERALD_BLOCK,
                "§aUpgrade Raffineria",
                "§7Aumenta velocità",
                "§7Riduce consumo"
        ));

        // Info
        inventory.setItem(15, createItem(
                Material.PAPER,
                "§eInformazioni",
                "§7Livello: §e" + level,
                "§7Processo: §eCarbone → Petrolio"
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
package org.RisingSMP.factory.gui;

import org.RisingSMP.factory.machines.GeneratorMachine;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GeneratorUpgradeGUI {

    private final Inventory inv;
    private final GeneratorMachine generator;

    public GeneratorUpgradeGUI(GeneratorMachine generator) {
        this.generator = generator;
        this.inv = Bukkit.createInventory(null, 9, "Upgrade Generatore");

        build();
    }

    public Inventory getInventory() {
        return inv;
    }

    private void build() {
        inv.clear();

        // Info generatore
        ItemStack info = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta meta = info.getItemMeta();
        meta.setDisplayName("§6Generatore");
        meta.setLore(java.util.List.of(
                "§7Livello: §e" + generator.getLevel(),
                "§7Range: §e" + generator.getRange()
        ));
        info.setItemMeta(meta);

        // Bottone upgrade
        ItemStack upgrade = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta uMeta = upgrade.getItemMeta();
        uMeta.setDisplayName("§aUPGRADE");
        upgrade.setItemMeta(uMeta);

        inv.setItem(4, info);
        inv.setItem(7, upgrade);
    }

    public void upgrade() {
        generator.upgrade();
        build(); // refresh GUI
    }
}
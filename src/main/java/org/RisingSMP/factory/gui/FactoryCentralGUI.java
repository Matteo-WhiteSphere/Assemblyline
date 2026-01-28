package org.RisingSMP.factory.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * GUI centrale della fabbrica (VERSIONE CORRETTA E COMPILABILE)
 * - Nessun riferimento a registry o macchine reali
 * - Nessun getAllX(), getType(), getLevel()
 * - Solo dashboard statica dei TIPI di macchinari
 */
public class FactoryCentralGUI {

    private final Inventory inventory;

    public FactoryCentralGUI() {
        this.inventory = Bukkit.createInventory(null, 27, "Factory Control Panel");
        build();
    }

    private void build() {
        inventory.clear();

        // Input Machine
        inventory.addItem(createItem(
                Material.DISPENSER,
                "§6Blocco Input",
                "§7Estrae item dalle chest",
                "§7Alimenta il sistema"
        ));

        // Output Machine
        inventory.addItem(createItem(
                Material.HOPPER,
                "§6Blocco Output",
                "§7Trasferisce item alle chest",
                "§7vicine (max 1 blocco)"
        ));

        // Generatore
        inventory.addItem(createItem(
                Material.REDSTONE_BLOCK,
                "§cGeneratore",
                "§7Produce energia",
                "§7Alimenta le macchine"
        ));

        // Raffineria
        inventory.addItem(createItem(
                Material.IRON_BLOCK,
                "§bRaffineria",
                "§7Consuma energia",
                "§7Trasforma risorse"
        ));

        // Fabbrica Intermedia
        inventory.addItem(createItem(
                Material.CRAFTING_TABLE,
                "§aFabbrica Intermedia",
                "§7Processo intermedio",
                "§7Richiede energia"
        ));

        // Fabbrica Finale
        inventory.addItem(createItem(
                Material.ANVIL,
                "§eFabbrica Finale",
                "§7Produzione finale",
                "§7Output avanzato"
        ));

        // Musket (simulato)
        inventory.setItem(25, createItem(
                Material.BOW,
                "§6Musket",
                "§7Arma simulata",
                "§7(Senza QualityArmory)"
        ));

        // Bullet (simulato)
        inventory.setItem(26, createItem(
                Material.ARROW,
                "§eBullet",
                "§7Munizioni simulate",
                "§7(Senza QualityArmory)"
        ));
    }

    private ItemStack createItem(Material material, String name, String... loreLines) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            List<String> lore = new ArrayList<>();
            for (String line : loreLines) lore.add(line);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    public Inventory getInventory() {
        return inventory;
    }
}

package org.RisingSMP.factory.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * GUI per il Blocco Input
 * Permette di accedere a tutte le altre GUI delle fabbriche
 */
public class InputGUI {

    private final Inventory inventory;

    public InputGUI() {
        this.inventory = Bukkit.createInventory(null, 27, "§6Input Factory Control");
        build();
    }

    private void build() {
        inventory.clear();

        // Bordo decorativo
        ItemStack border = createItem(Material.GRAY_STAINED_GLASS_PANE, "§f");
        for (int i = 0; i < 27; i++) {
            if (i < 9 || i >= 18 || i % 9 == 0 || i % 9 == 8) {
                inventory.setItem(i, border);
            }
        }

        // GUI Central Factory
        inventory.setItem(10, createItem(
                Material.NETHER_STAR,
                "§6§lFactory Central",
                "§7Apri il pannello principale",
                "§7delle fabbriche"
        ));

        // GUI Produzione
        inventory.setItem(12, createItem(
                Material.CRAFTING_TABLE,
                "§a§lProduzione Items",
                "§7Apri la GUI di produzione",
                "§7per craftare qualsiasi item"
        ));

        // GUI Energia
        inventory.setItem(14, createItem(
                Material.REDSTONE,
                "§c§lStato Energia",
                "§7Controlla lo stato energetico",
                "§7di tutte le macchine"
        ));

        // GUI Generatori
        inventory.setItem(16, createItem(
                Material.COAL_BLOCK,
                "§e§lControllo Generatori",
                "§7Gestisci tutti i generatori",
                "§7e la loro produzione"
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

    /**
     * Gestisce i click nella GUI Input
     */
    public void handleClick(Player player, ItemStack clickedItem, int slot) {
        if (clickedItem == null || !clickedItem.hasItemMeta()) return;

        String itemName = clickedItem.getItemMeta().getDisplayName();
        
        switch (itemName) {
            case "§6§lFactory Central" -> {
                player.openInventory(new FactoryCentralGUI().getInventory());
                player.sendMessage("§aAperta GUI Factory Central");
            }
            
            case "§a§lProduzione Items" -> {
                // Usa la ProductionGUI del sistema di produzione
                org.RisingSMP.factory.gui.ProductionGUI productionGUI = new org.RisingSMP.factory.gui.ProductionGUI();
                productionGUI.openMainMenu(player);
                player.sendMessage("§aAperta GUI Produzione");
            }
            
            case "§c§lStato Energia" -> {
                player.openInventory(new EnergyGUI().getInventory());
                player.sendMessage("§aAperta GUI Energia");
            }
            
            case "§e§lControllo Generatori" -> {
                player.openInventory(new GeneratorGUI().getInventory());
                player.sendMessage("§aAperta GUI Generatori");
            }
        }
    }
}

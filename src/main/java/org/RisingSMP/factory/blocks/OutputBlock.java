package org.RisingSMP.factory.blocks;

import org.RisingSMP.factory.items.FactoryItems;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

/**
 * Rappresenta il blocco Output che trasferisce item alle chest vicine
 */
public class OutputBlock {

    public static ItemStack createItem() {
        ItemStack item = new ItemStack(Material.HOPPER);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§6Blocco Output");
            meta.setLore(java.util.Arrays.asList(
                "§7Trasferisce item alle chest",
                "§7vicine (max 1 blocco)",
                "§7Richiede: 1 energia/item"
            ));
            
            // Aggiungi PDC per identificazione
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            pdc.set(FactoryItems.FACTORY_KEY, PersistentDataType.STRING, "OUTPUT");
            
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    /**
     * Controlla se un blocco è un OutputBlock
     */
    public static boolean isOutputBlock(Block block) {
        if (block.getType() != Material.HOPPER) return false;
        
        PersistentDataContainer pdc = block.getChunk().getPersistentDataContainer();
        String type = pdc.get(FactoryItems.FACTORY_KEY, PersistentDataType.STRING);
        return "OUTPUT".equals(type);
    }
}

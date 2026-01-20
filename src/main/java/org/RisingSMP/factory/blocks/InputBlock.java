package org.RisingSMP.factory.blocks;

import org.RisingSMP.factory.FactoryItems;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class InputBlock {

    public static ItemStack create() {
        ItemStack item = new ItemStack(Material.DISPENSER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6Blocco Input");
        meta.getPersistentDataContainer().set(
                FactoryItems.FACTORY_KEY,
                PersistentDataType.STRING,
                "INPUT"
        );
        item.setItemMeta(meta);
        return item;
    }
}
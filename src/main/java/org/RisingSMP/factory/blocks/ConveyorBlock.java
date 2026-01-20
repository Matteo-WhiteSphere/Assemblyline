package org.RisingSMP.factory.blocks;

import org.RisingSMP.factory.FactoryItems;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ConveyorBlock {

    public static ItemStack create() {
        ItemStack item = new ItemStack(Material.PISTON);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6Nastro Trasportatore");
        meta.getPersistentDataContainer().set(
                FactoryItems.FACTORY_KEY,
                PersistentDataType.STRING,
                "BELT"
        );
        item.setItemMeta(meta);
        return item;
    }
}
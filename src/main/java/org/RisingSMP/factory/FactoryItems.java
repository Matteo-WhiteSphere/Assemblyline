package org.RisingSMP.factory;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public class FactoryItems {

    // Chiave unica per identificare i blocchi custom
    public static NamespacedKey FACTORY_KEY;

    public static final NamespacedKey LEVEL_KEY =
            new NamespacedKey(Factory.instance, "level");

    // Metodo per inizializzare la chiave (da chiamare in onEnable del main)
    public static void init() {
        FACTORY_KEY = new NamespacedKey(org.RisingSMP.factory.Factory.instance, "factory");
    }

    // Metodo per creare un blocco custom
    public static ItemStack createBlock(Material material, String displayName, String id) {
        ItemStack item = new ItemStack(material);
        // Qui si possono aggiungere metadati o altre proprietà per il blocco
        return item;
    }

    // Blocchi generatore
    public static ItemStack NUCLEAR_GENERATOR = createBlock(
            Material.EMERALD_BLOCK,
            "§5Generatore Nucleare",
            "GENERATOR_NUCLEAR"
    );
}
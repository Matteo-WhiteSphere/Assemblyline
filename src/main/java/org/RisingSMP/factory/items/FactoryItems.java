package org.RisingSMP.factory.items;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.RisingSMP.factory.Factory;
import org.RisingSMP.factory.energy.EnergyManager;

public class FactoryItems {

    /* =========================
       KEYS
       ========================= */
    public static final NamespacedKey LEVEL_KEY =
            new NamespacedKey(Factory.instance, "level");

    public static NamespacedKey TYPE_KEY;

    public static final NamespacedKey FACTORY_KEY =
            new NamespacedKey(Factory.instance, "factory_type");

    public static final NamespacedKey FACING_KEY =
            new NamespacedKey(Factory.instance, "facing");

    public static final NamespacedKey LAST_PULL_KEY =
            new NamespacedKey(Factory.instance, "last_pull");

    /* =========================
       ITEMS / BLOCKS
       ========================= */

    public static ItemStack GENERATOR;
    public static ItemStack REFINERY;
    public static ItemStack OIL_BLOCK;

    /* =========================
       INIT
       ========================= */

    public static void init() {

        TYPE_KEY = FACTORY_KEY;

        // ===== GENERATORE =====
        GENERATOR = createBlock(
                Material.BLAST_FURNACE,
                "§6Generatore",
                "GENERATOR"
        );

        // ===== RAFFINERIA =====
        REFINERY = createBlock(
                Material.FURNACE,
                "§7Raffineria",
                "REFINERY"
        );

        // ===== PETROLIO =====
        OIL_BLOCK = createItem(
                Material.COAL_BLOCK,
                "§0Petrolio",
                "OIL"
        );
    }

    /* =========================
       STANDARD FACTORY BLOCKS
       ========================= */

    public static ItemStack inputBlock() {
        return createBlock(Material.DISPENSER, "§6Blocco Input", "INPUT");
    }

    public static ItemStack conveyor() {
        return createBlock(Material.PISTON, "§6Nastro Trasportatore", "BELT");
    }

    public static ItemStack intermediateFactory() {
        return createBlock(Material.DISPENSER, "§6Fabbrica Intermedia", "INTERMEDIATE");
    }

    public static ItemStack finalFactory() {
        return createBlock(Material.DISPENSER, "§6Fabbrica Finale", "FINAL");
    }

    public static boolean isGenerator(Block block) {
        if (block == null || block.getType() != Material.BLAST_FURNACE) return false;
        ItemStack fake = new ItemStack(block.getType());
        // opzionale: puoi usare PDC per confermare tipo "GENERATOR"
        return true;
    }

    /* =========================
       INTERNAL HELPERS
       ========================= */

    private static ItemStack createBlock(Material material, String name, String type) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(name);
        meta.getPersistentDataContainer().set(
                FACTORY_KEY,
                PersistentDataType.STRING,
                type
        );

        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack createItem(Material material, String name, String type) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(name);
        meta.getPersistentDataContainer().set(
                FACTORY_KEY,
                PersistentDataType.STRING,
                type
        );

        item.setItemMeta(meta);
        return item;
    }
}
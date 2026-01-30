package org.RisingSMP.factory.production;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class WeaponProduction {
    
    public enum WeaponType {
        MUSKET(Material.IRON_INGOT, 8, "Moschetto", 30);
        
        private final Material material;
        private final int amount;
        private final String displayName;
        private final int productionTime;
        
        WeaponType(Material material, int amount, String displayName, int productionTime) {
            this.material = material;
            this.amount = amount;
            this.displayName = displayName;
            this.productionTime = productionTime;
        }
        
        public Material getMaterial() { return material; }
        public int getAmount() { return amount; }
        public String getDisplayName() { return displayName; }
        public int getProductionTime() { return productionTime; }
    }
    
    public enum AmmoType {
        MUSKET_BALL(Material.IRON_INGOT, 1, "Palla da Moschetto", 3);
        
        private final Material material;
        private final int amount;
        private final String displayName;
        private final int productionTime;
        
        AmmoType(Material material, int amount, String displayName, int productionTime) {
            this.material = material;
            this.amount = amount;
            this.displayName = displayName;
            this.productionTime = productionTime;
        }
        
        public Material getMaterial() { return material; }
        public int getAmount() { return amount; }
        public String getDisplayName() { return displayName; }
        public int getProductionTime() { return productionTime; }
    }
    
    private static final Map<WeaponType, ItemStack> weaponCache = new HashMap<>();
    private static final Map<AmmoType, ItemStack> ammoCache = new HashMap<>();
    
    static {
        // Inizializza cache armi
        for (WeaponType type : WeaponType.values()) {
            ItemStack weapon = createWeapon(type);
            weaponCache.put(type, weapon);
        }
        
        // Inizializza cache munizioni
        for (AmmoType type : AmmoType.values()) {
            ItemStack ammo = createAmmo(type);
            ammoCache.put(type, ammo);
        }
    }
    
    private static ItemStack createWeapon(WeaponType type) {
        // Moschetto simulato con item personalizzato
        ItemStack weapon = new ItemStack(Material.IRON_HOE);
        ItemMeta meta = weapon.getItemMeta();
        
        if (meta != null) {
            meta.displayName(net.kyori.adventure.text.Component.text("§c" + type.getDisplayName()));
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            
            // Custom lore per statistiche moschetto
            meta.lore(java.util.List.of(
                net.kyori.adventure.text.Component.text("§7Danno: §a25"),
                net.kyori.adventure.text.Component.text("§7Precisione: §a70%"),
                net.kyori.adventure.text.Component.text("§7Caduta: §a2/sec"),
                net.kyori.adventure.text.Component.text("§7Ricarica: §e3 secondi"),
                net.kyori.adventure.text.Component.text("§7Tempo produzione: §e" + type.getProductionTime() + "s")
            ));
            
            weapon.setItemMeta(meta);
        }
        
        return weapon;
    }
    
    private static ItemStack createAmmo(AmmoType type) {
        // Palle da moschetto con firework star
        ItemStack ammo = new ItemStack(Material.FIREWORK_STAR, type.getAmount());
        ItemMeta meta = ammo.getItemMeta();
        
        if (meta != null) {
            meta.displayName(net.kyori.adventure.text.Component.text("§e" + type.getDisplayName()));
            meta.lore(java.util.List.of(
                net.kyori.adventure.text.Component.text("§7Quantità: §a" + type.getAmount()),
                net.kyori.adventure.text.Component.text("§7Calibro: §e.69"),
                net.kyori.adventure.text.Component.text("§7Tempo produzione: §e" + type.getProductionTime() + "s")
            ));
            ammo.setItemMeta(meta);
        }
        
        return ammo;
    }
    
    public static ItemStack getWeapon(WeaponType type) {
        return weaponCache.get(type).clone();
    }
    
    public static ItemStack getAmmo(AmmoType type) {
        return ammoCache.get(type).clone();
    }
    
    public static boolean canProduceWeapon(WeaponType type, ItemStack[] ingredients) {
        int required = type.getAmount();
        
        for (ItemStack item : ingredients) {
            if (item != null && item.getType() == type.getMaterial()) {
                required -= item.getAmount();
                if (required <= 0) return true;
            }
        }
        
        return false;
    }
    
    public static boolean canProduceAmmo(AmmoType type, ItemStack[] ingredients) {
        int required = type.getAmount();
        
        for (ItemStack item : ingredients) {
            if (item != null && item.getType() == type.getMaterial()) {
                required -= item.getAmount();
                if (required <= 0) return true;
            }
        }
        
        return false;
    }
    
    public static int getWeaponProductionCost(WeaponType type) {
        return type.getProductionTime() * 2; // Costo energetico
    }
    
    public static int getAmmoProductionCost(AmmoType type) {
        return type.getProductionTime(); // Costo energetico
    }
}

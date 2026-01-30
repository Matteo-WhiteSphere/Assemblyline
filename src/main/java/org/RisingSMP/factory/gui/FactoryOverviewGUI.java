package org.RisingSMP.factory.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.RisingSMP.factory.machines.Machine;
import org.RisingSMP.factory.registry.MachineRegistry;
import org.RisingSMP.factory.notifications.NotificationManager;

import java.util.HashMap;
import java.util.Map;

public class FactoryOverviewGUI {
    
    private static final String TITLE = "§6Sistema Factory - Panoramica";
    private static final Map<Player, Long> lastOpen = new HashMap<>();
    private static final long OPEN_COOLDOWN = 1000; // 1 secondo
    
    public static void openOverview(Player player) {
        // Check cooldown
        long currentTime = System.currentTimeMillis();
        if (lastOpen.containsKey(player) && currentTime - lastOpen.get(player) < OPEN_COOLDOWN) {
            return;
        }
        lastOpen.put(player, currentTime);
        
        Inventory inv = Bukkit.createInventory(null, 54, net.kyori.adventure.text.Component.text(TITLE));
        
        // Bordo decorativo
        ItemStack border = createItem(Material.GRAY_STAINED_GLASS_PANE, "§r", new String[]{});
        for (int i = 0; i < 54; i++) {
            if (i < 9 || i >= 45 || i % 9 == 0 || i % 9 == 8) {
                inv.setItem(i, border);
            }
        }
        
        // Statistiche generali
        inv.setItem(4, createItem(Material.BEACON, "§6Statistiche Factory", new String[]{
            "§7Macchine attive: §a" + MachineRegistry.getAll().size(),
            "§7Produzione totale: §e" + calculateTotalProduction(),
            "§7Consumo energetico: §c" + calculateTotalConsumption() + " ⚡/s"
        }));
        
        // Pannello macchine
        inv.setItem(19, createMachinePanel());
        inv.setItem(20, createEnergyPanel());
        inv.setItem(21, createProductionPanel());
        
        // Controlli rapidi
        inv.setItem(22, createItem(Material.COMMAND_BLOCK, "§eControlli Rapid", new String[]{
            "§7Click per azioni rapide",
            "",
            "§a▶ Avvia tutte le macchine",
            "§c⏸ Ferma tutte le macchine",
            "§6⚡ Gestione energia"
        }));
        
        // Informazioni
        inv.setItem(49, createItem(Material.BOOK, "§7Informazioni", new String[]{
            "§7Versione: §e1.0.0",
            "§7Developer: §bRisingSMP",
            "",
            "§7Click dx su macchine per gestione",
            "§7Usa §e/factoryconfig §7per configurare"
        }));
        
        player.openInventory(inv);
        NotificationManager.sendNotification(player, NotificationManager.NotificationType.INFO, 
            "Panoramica factory aperta");
    }
    
    private static ItemStack createMachinePanel() {
        int activeMachines = (int) MachineRegistry.getAll().stream()
                .filter(m -> m instanceof org.RisingSMP.factory.machines.GeneratorMachine)
                .count();
        
        return createItem(Material.IRON_BLOCK, "§aMacchine Attive", new String[]{
            "§7Generatori: §e" + activeMachines,
            "§7Raffinerie: §e" + countMachineType("REFINERY"),
            "§7Intermedie: §e" + countMachineType("INTERMEDIATE"),
            "§7Finali: §e" + countMachineType("FINAL")
        });
    }
    
    private static ItemStack createEnergyPanel() {
        int production = calculateTotalProduction();
        int consumption = calculateTotalConsumption();
        int efficiency = production > 0 ? (consumption * 100) / production : 0;
        
        Material material = efficiency < 50 ? Material.REDSTONE_BLOCK : 
                           efficiency < 80 ? Material.GOLD_BLOCK : 
                           Material.EMERALD_BLOCK;
        
        return createItem(material, "§6Stato Energetico", new String[]{
            "§7Produzione: §a" + production + " ⚡/s",
            "§7Consumo: §c" + consumption + " ⚡/s",
            "§7Efficienza: §e" + efficiency + "%",
            "",
            efficiency < 50 ? "§c⚠ Energia insufficiente!" :
            efficiency < 80 ? "§e⚡ Energia stabile" :
            "§a✓ Energia ottimale"
        });
    }
    
    private static ItemStack createProductionPanel() {
        return createItem(Material.CRAFTING_TABLE, "§bProduzione", new String[]{
            "§7Armi prodotte: §e" + getWeaponCount(),
            "§7Munizioni prodotte: §e" + getAmmoCount(),
            "§7Materiali processati: §e" + getMaterialCount(),
            "",
            "§7Ultimo ciclo: §a" + getLastProductionTime() + "s fa"
        });
    }
    
    private static int calculateTotalProduction() {
        return MachineRegistry.getAll().stream()
                .filter(m -> m instanceof org.RisingSMP.factory.machines.GeneratorMachine)
                .mapToInt(m -> ((org.RisingSMP.factory.machines.GeneratorMachine) m).getProduction())
                .sum();
    }
    
    private static int calculateTotalConsumption() {
        return MachineRegistry.getAll().stream()
                .mapToInt(Machine::getEnergyCost)
                .sum();
    }
    
    private static int countMachineType(String type) {
        return (int) MachineRegistry.getAll().stream()
                .filter(m -> m.getBlock().getType().name().contains(type))
                .count();
    }
    
    private static int getWeaponCount() {
        // Simula conteggio armi prodotte
        return (int) (Math.random() * 100);
    }
    
    private static int getAmmoCount() {
        // Simula conteggio munizioni prodotte
        return (int) (Math.random() * 500);
    }
    
    private static int getMaterialCount() {
        // Simula conteggio materiali processati
        return (int) (Math.random() * 1000);
    }
    
    private static long getLastProductionTime() {
        // Simula tempo dell'ultima produzione
        return (long) (Math.random() * 60);
    }
    
    private static ItemStack createItem(Material material, String name, String[] lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.displayName(net.kyori.adventure.text.Component.text(name));
            if (lore.length > 0) {
                meta.lore(java.util.Arrays.stream(lore)
                    .map(net.kyori.adventure.text.Component::text)
                    .toList());
            }
            item.setItemMeta(meta);
        }
        
        return item;
    }
}

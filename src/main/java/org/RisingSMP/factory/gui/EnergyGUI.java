package org.RisingSMP.factory.gui;

import org.RisingSMP.factory.energy.EnergyManager;
import org.RisingSMP.factory.items.FactoryItems;
import org.RisingSMP.factory.machines.GeneratorMachine;
import org.RisingSMP.factory.machines.GeneratorMachine.GeneratorType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EnergyGUI {

    private final Inventory inv;

    public EnergyGUI() {
        inv = Bukkit.createInventory(null, 9, "Stato Energia");
        updateGUI();
    }

    public Inventory getInventory() {
        return inv;
    }

    public void updateGUI() {
        inv.clear();

        // Slot 0-3: Generatori
        updateGeneratorSlot(0, "GENERATOR_OIL", "Generatore Petrolio");
        updateGeneratorSlot(1, "GENERATOR_COAL", "Generatore Carbone");
        updateGeneratorSlot(2, "GENERATOR_SOLAR", "Generatore Solare");
        updateGeneratorSlot(3, "GENERATOR_WIND", "Generatore Eolico");

        // Slot 4-5: Fabbriche (per ora dummy)
        updateFactorySlot(4, "INTERMEDIATE", "Fabbrica Intermedia");
        updateFactorySlot(5, "FINAL", "Fabbrica Finale");

        // Slot 6: Raffineria
        updateFactorySlot(6, "REFINERY", "Raffineria");
    }

    private void updateGeneratorSlot(int slot, String typeKey, String displayName) {
        Material mat = Material.RED_WOOL;

        // Scansione generatori caricati nel mondo
        for (var world : Bukkit.getWorlds()) {
            for (var chunk : world.getLoadedChunks()) {
                int minY = world.getMinHeight();
                int maxY = world.getMaxHeight();
                int bx = chunk.getX() << 4;
                int bz = chunk.getZ() << 4;

                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        for (int y = minY; y < maxY; y++) {
                            Block block = world.getBlockAt(bx + x, y, bz + z);
                            if (!(block.getState() instanceof TileState state)) continue;
                            String type = state.getPersistentDataContainer()
                                    .get(FactoryItems.FACTORY_KEY, org.bukkit.persistence.PersistentDataType.STRING);
                            if (type == null || !type.equals(typeKey)) continue;

                            GeneratorMachine gen = new GeneratorMachine(block, GeneratorType.valueOf(typeKey.split("_")[1]));
                            gen.tick();
                            if (gen.isActive()) mat = Material.LIME_WOOL;
                        }
                    }
                }
            }
        }

        inv.setItem(slot, createItem(mat, displayName));
    }

    private void updateFactorySlot(int slot, String typeKey, String displayName) {
        Material mat = Material.RED_WOOL;

        // Dummy check: se almeno una macchina con energia nelle chunk
        for (var world : Bukkit.getWorlds()) {
            for (var chunk : world.getLoadedChunks()) {
                int minY = world.getMinHeight();
                int maxY = world.getMaxHeight();
                int bx = chunk.getX() << 4;
                int bz = chunk.getZ() << 4;

                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        for (int y = minY; y < maxY; y++) {
                            Block block = world.getBlockAt(bx + x, y, bz + z);
                            if (!(block.getState() instanceof TileState state)) continue;
                            String type = state.getPersistentDataContainer()
                                    .get(FactoryItems.FACTORY_KEY, org.bukkit.persistence.PersistentDataType.STRING);
                            if (type == null || !type.equals(typeKey)) continue;

                            if (EnergyManager.hasEnergy(block)) mat = Material.LIME_WOOL;
                        }
                    }
                }
            }
        }

        inv.setItem(slot, createItem(mat, displayName));
    }

    private ItemStack createItem(Material mat, String name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }
}

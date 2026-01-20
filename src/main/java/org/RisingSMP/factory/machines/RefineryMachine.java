package org.RisingSMP.factory.machines;

import org.RisingSMP.factory.items.FactoryItems;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Item;
import org.RisingSMP.factory.energy.EnergyManager;

public class RefineryMachine extends Machine {

    public RefineryMachine(Block block) {
        super(block);
    }

    @Override
    public void process(Item item) {

        ItemStack input = item.getItemStack();

        // input: blocco di carbone
        if (input.getType() != Material.COAL_BLOCK) return;

        // output davanti (per ora fisso NORTH)
        Block out = block.getRelative(BlockFace.NORTH);
        if (!(out.getState() instanceof Chest chest)) return;

        Inventory inv = chest.getBlockInventory();
        if (inv.firstEmpty() == -1) return;

        // produce petrolio
        inv.addItem(FactoryItems.OIL_BLOCK.clone());
        item.remove();
    }
}
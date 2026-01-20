package org.RisingSMP.factory.machines;

import org.RisingSMP.factory.energy.EnergyManager;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class InputMachine extends Machine {

    public InputMachine(Block block) {
        super(block);
    }

    @Override
    public void process(Item ignored) {
        // INPUT non processa item sopra
        // lavora solo con pullFromChest
    }

    public void pullFromChest() {

        if (!EnergyManager.hasEnergy(block)) return;

        // direzione (per ora fissa)
        BlockFace facing = BlockFace.NORTH;
        Block back = block.getRelative(facing.getOppositeFace());

        if (!(back.getState() instanceof Chest chest)) return;

        Inventory inv = chest.getBlockInventory();

        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (stack == null) continue;

            // togli 1 item
            ItemStack drop = stack.clone();
            drop.setAmount(1);

            stack.setAmount(stack.getAmount() - 1);
            inv.setItem(i, stack.getAmount() > 0 ? stack : null);

            // spawn item sopra la macchina
            Item item = block.getWorld().dropItem(
                    block.getLocation().add(0.5, 1, 0.5),
                    drop
            );
            item.setVelocity(new Vector(0, 0, 0));

            break;
        }
    }
}
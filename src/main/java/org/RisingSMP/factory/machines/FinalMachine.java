package org.RisingSMP.factory.machines;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.TileState;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.RisingSMP.factory.energy.EnergyManager;
import org.RisingSMP.factory.Factory;
import org.RisingSMP.factory.gui.FactoryGUI;

public class FinalMachine extends Machine {

    public FinalMachine(Block block) {
        super(block);
    }

    @Override
    public void process(Item item) {

        ItemStack input = item.getItemStack();
        if (!EnergyManager.hasEnergy(block)) return;
        if (input.getType() != Material.IRON_INGOT) return;

        if (!(block.getState() instanceof TileState state)) return;

        // direzione
        String facingStr = state.getPersistentDataContainer()
                .getOrDefault(
                        new NamespacedKey(org.RisingSMP.factory.Factory.instance, "facing"),
                        PersistentDataType.STRING,
                        "NORTH"
                );
        BlockFace facing = BlockFace.valueOf(facingStr);

        // chest output
        Block outBlock = block.getRelative(facing);
        if (!(outBlock.getState() instanceof Chest chest)) return;

        Inventory outInv = chest.getBlockInventory();
        if (outInv.firstEmpty() == -1) return;

        // arma scelta
        String weaponId = org.RisingSMP.factory.gui.FactoryGUI.factoryWeapons
                .getOrDefault("FINAL", "ak47")
                .toLowerCase();

        // trova un player online (necessario per QA)
        Player p = Bukkit.getOnlinePlayers().stream().findFirst().orElse(null);
        if (p == null) return;

        // esegue comando QA
        Bukkit.dispatchCommand(
                Bukkit.getConsoleSender(),
                "qa give " + p.getName() + " " + weaponId + " 1"
        );

        // tick dopo: sposta arma dalla inventory del player alla chest
        Bukkit.getScheduler().runTaskLater(
                org.RisingSMP.factory.Factory.instance,
                () -> {
                    for (ItemStack stack : p.getInventory().getContents()) {
                        if (stack == null) continue;

                        outInv.addItem(stack.clone());
                        p.getInventory().remove(stack);
                        break;
                    }
                },
                1L
        );

        // consuma input
        item.remove();
    }
}
package org.RisingSMP.factory.machines;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.RisingSMP.factory.gui.FactoryGUI;
import org.RisingSMP.factory.energy.EnergyManager;

import java.util.Map;

public class IntermediateMachine extends Machine {

    public IntermediateMachine(Block block) {
        super(block);
    }

    @Override
    public void process(Item item) {
        ItemStack stack = item.getItemStack();
        Map<Material, Material> recipes = FactoryGUI.factoryRecipes.get("INTERMEDIATE");

        if (!EnergyManager.hasEnergy(block)) return;
        if (recipes != null && recipes.containsKey(stack.getType())) {
            stack.setType(recipes.get(stack.getType()));
            item.setItemStack(stack);
        }
    }
}
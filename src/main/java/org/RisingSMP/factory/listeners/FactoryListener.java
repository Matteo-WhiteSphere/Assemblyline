package org.RisingSMP.factory.listeners;

import org.RisingSMP.factory.machines.InputMachine;
import org.RisingSMP.factory.Factory;
import org.RisingSMP.factory.items.FactoryItems;
import org.RisingSMP.factory.gui.FactoryGUI;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.RisingSMP.factory.machines.ConveyorMachine;
import org.RisingSMP.factory.machines.IntermediateMachine;
import org.RisingSMP.factory.machines.FinalMachine;
import org.RisingSMP.factory.machines.Machine;
import org.RisingSMP.factory.energy.EnergyManager;

import java.util.Map;

public class FactoryListener implements Listener {

    private final Factory plugin;

    public FactoryListener(Factory plugin) {
        this.plugin = plugin;
        startConveyorTask();
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if (!e.getItemInHand().hasItemMeta()) return;

        TileState state = (TileState) e.getBlockPlaced().getState();
        String type = e.getItemInHand().getItemMeta().getPersistentDataContainer()
                .get(FactoryItems.FACTORY_KEY, PersistentDataType.STRING);
        if (type != null) {
            state.getPersistentDataContainer().set(
                    FactoryItems.FACTORY_KEY,
                    PersistentDataType.STRING,
                    type
            );

            // salva la direzione del conveyor
            state.getPersistentDataContainer().set(
                    new org.bukkit.NamespacedKey(Factory.instance, "facing"),
                    PersistentDataType.STRING,
                    e.getBlockPlaced().getFace(e.getPlayer().getLocation().getBlock()).name()
            );
            state.update();
        }
    }

    private void startConveyorTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getWorlds().forEach(world -> {
                    for (Item item : world.getEntitiesByClass(Item.class)) {

                        Block blockBelow = item.getLocation().subtract(0, 1, 0).getBlock();
                        if (!(blockBelow.getState() instanceof TileState state)) continue;

                        String type = state.getPersistentDataContainer()
                                .get(FactoryItems.FACTORY_KEY, PersistentDataType.STRING);

                        if (type == null) continue;

                        Machine machine = null;

                        switch (type) {
                            case "INPUT" -> {
                                InputMachine input = new InputMachine(blockBelow);
                                input.pullFromChest();
                            }
                            case "BELT" -> machine = new ConveyorMachine(blockBelow);
                            case "INTERMEDIATE" -> machine = new IntermediateMachine(blockBelow);
                            case "FINAL" -> machine = new FinalMachine(blockBelow);
                        }

                        if (machine != null) {
                            machine.process(item);
                        }
                    }
                });
            }
        }.runTaskTimer(plugin, 0L, 5L);
    }

    private void moveItem(Item item, TileState state) {
        String facingStr = state.getPersistentDataContainer()
                .get(new org.bukkit.NamespacedKey(Factory.instance, "facing"), PersistentDataType.STRING);
        Vector dir = switch (facingStr != null ? facingStr : "NORTH") {
            case "NORTH" -> new Vector(0, 0, -0.2);
            case "SOUTH" -> new Vector(0, 0, 0.2);
            case "WEST" -> new Vector(-0.2, 0, 0);
            case "EAST" -> new Vector(0.2, 0, 0);
            default -> new Vector(0, 0, 0.2);
        };
        item.setVelocity(dir);
    }

    // Fabbrica configurabile tramite GUI
    private void processFactory(Item item, String factoryType) {
        ItemStack stack = item.getItemStack();
        Map<org.bukkit.Material, org.bukkit.Material> recipes = FactoryGUI.factoryRecipes.get(factoryType);

        if (recipes != null && recipes.containsKey(stack.getType())) {
            stack.setType(recipes.get(stack.getType()));
            item.setItemStack(stack);
        }
    }
}
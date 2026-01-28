package org.RisingSMP.factory.listeners;

import org.RisingSMP.factory.Factory;
import org.RisingSMP.factory.gui.GeneratorUpgradeGUI;
import org.RisingSMP.factory.gui.FactoryGUI;
import org.RisingSMP.factory.items.FactoryItems;
import org.RisingSMP.factory.machines.*;
import org.RisingSMP.factory.registry.MachineRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Map;

public class FactoryListener implements Listener {

    private final Factory plugin;

    public FactoryListener(Factory plugin) {
        this.plugin = plugin;
        startConveyorTask();
    }

    // =========================
    // PIAZZAMENTO MACCHINE
    // =========================
    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if (!e.getItemInHand().hasItemMeta()) return;

        TileState state = (TileState) e.getBlockPlaced().getState();

        String type = e.getItemInHand().getItemMeta()
                .getPersistentDataContainer()
                .get(FactoryItems.FACTORY_KEY, PersistentDataType.STRING);

        if (type == null) return;

        state.getPersistentDataContainer().set(
                FactoryItems.FACTORY_KEY,
                PersistentDataType.STRING,
                type
        );

        state.getPersistentDataContainer().set(
                FactoryItems.LEVEL_KEY,
                PersistentDataType.INTEGER,
                1
        );

        state.getPersistentDataContainer().set(
                new NamespacedKey(Factory.instance, "facing"),
                PersistentDataType.STRING,
                e.getBlockPlaced()
                        .getFace(e.getPlayer().getLocation().getBlock())
                        .name()
        );

        state.update();
    }

    // =========================
    // TASK CATENA DI MONTAGGIO
    // =========================
    private void startConveyorTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getWorlds().forEach(world -> {
                    for (Item item : world.getEntitiesByClass(Item.class)) {

                        Block blockBelow =
                                item.getLocation().subtract(0, 1, 0).getBlock();

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

    // =========================
    // MOVIMENTO ITEM (BELT)
    // =========================
    private void moveItem(Item item, TileState state) {

        String facingStr = state.getPersistentDataContainer()
                .get(new NamespacedKey(Factory.instance, "facing"),
                        PersistentDataType.STRING);

        Vector dir = switch (facingStr != null ? facingStr : "NORTH") {
            case "NORTH" -> new Vector(0, 0, -0.2);
            case "SOUTH" -> new Vector(0, 0, 0.2);
            case "WEST" -> new Vector(-0.2, 0, 0);
            case "EAST" -> new Vector(0.2, 0, 0);
            default -> new Vector(0, 0, 0.2);
        };

        item.setVelocity(dir);
    }

    // =========================
    // GUI UPGRADE GENERATORE
    // =========================
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!event.getView().getTitle().equals("Upgrade Generatore")) return;

        event.setCancelled(true);

        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getType() != Material.EMERALD_BLOCK) return;

        Block target = player.getTargetBlockExact(5);
        if (target == null) return;

        if (!(target.getState() instanceof TileState state)) return;

        String type = state.getPersistentDataContainer()
                .get(FactoryItems.FACTORY_KEY, PersistentDataType.STRING);

        if (type == null) return;
        if (!type.startsWith("GENERATOR_")) return;

        GeneratorMachine.GeneratorType genType;
        try {
            genType = GeneratorMachine.GeneratorType.valueOf(
                    type.replace("GENERATOR_", "")
            );
        } catch (IllegalArgumentException ex) {
            return;
        }

        GeneratorMachine gen = new GeneratorMachine(target, genType);
        GeneratorUpgradeGUI gui = new GeneratorUpgradeGUI(gen);

        gui.upgrade();
    }
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        if (!event.getItemInHand().hasItemMeta()) return;

        var meta = event.getItemInHand().getItemMeta();
        if (meta == null) return;

        var pdc = meta.getPersistentDataContainer();
        String type = pdc.get(FactoryItems.FACTORY_KEY, PersistentDataType.STRING);
        if (type == null) return;

        Block block = event.getBlockPlaced();

        Machine machine = null;

        switch (type) {
            case "GENERATOR" -> machine = new GeneratorMachine(block);
            case "REFINERY" -> machine = new RefineryMachine(block);
            case "INPUT" -> machine = new InputMachine(block);
            case "OUTPUT" -> machine = new OutputMachine(block);
            case "INTERMEDIATE" -> machine = new IntermediateMachine(block);
            case "FINAL" -> machine = new FinalMachine(block);
        }

        if (machine != null) {
            MachineRegistry.register(block, machine);
        }
    }
    // =========================
    // CLICK DESTRO SU BLOCCHI MACCHINA
    // =========================
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock() == null) return;
        
        Block block = event.getClickedBlock();
        if (!(block.getState() instanceof TileState state)) return;
        
        String type = state.getPersistentDataContainer()
                .get(FactoryItems.FACTORY_KEY, PersistentDataType.STRING);
        
        if (type == null) return;
        
        // Impedisci l'azione di default (aprire chest, etc.)
        event.setCancelled(true);
        
        Player player = event.getPlayer();
        
        // Apri la GUI specifica per il tipo di macchina
        switch (type) {
            case "GENERATOR" -> {
                Machine machine = MachineRegistry.get(block);
                if (machine instanceof GeneratorMachine genMachine) {
                    player.openInventory(new org.RisingSMP.factory.gui.GeneratorGUI(genMachine).getInventory());
                }
            }
            case "REFINERY" -> {
                Machine machine = MachineRegistry.get(block);
                if (machine instanceof RefineryMachine refineryMachine) {
                    player.openInventory(new org.RisingSMP.factory.gui.RefineryGUI(refineryMachine).getInventory());
                }
            }
            case "INPUT" -> {
                player.sendMessage("§6[Input] Blocco di input selezionato!");
                player.sendMessage("§7Estrae item dalle chest e li deposita sopra");
            }
            case "OUTPUT" -> {
                player.sendMessage("§6[Output] Blocco di output selezionato!");
                player.sendMessage("§7Trasferisce item alle chest più vicine");
                player.sendMessage("§7Massimo 1 blocco di distanza");
            }
            case "INTERMEDIATE" -> {
                player.sendMessage("§a[Factory] Fabbrica Intermedia selezionata");
            }
            case "FINAL" -> {
                player.sendMessage("§e[Factory] Fabbrica Finale selezionata");
            }
            default -> {
                player.sendMessage("§c[Factory] Macchina non riconosciuta: " + type);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        MachineRegistry.unregister(event.getBlock());
    }
}
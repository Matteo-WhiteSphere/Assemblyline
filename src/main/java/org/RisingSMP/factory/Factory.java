package org.RisingSMP.factory;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.RisingSMP.factory.items.FactoryItems;
import org.RisingSMP.factory.machines.GeneratorMachine;
import org.RisingSMP.factory.machines.GeneratorMachine.GeneratorType;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.block.TileState;
import org.RisingSMP.factory.commands.GiveFactoryCommand;
import org.RisingSMP.factory.commands.FactoryConfigCommand;
import org.RisingSMP.factory.listeners.FactoryListener;
import org.RisingSMP.factory.gui.FactoryGUI;
import org.RisingSMP.factory.commands.EnergyGUICommand;
import org.RisingSMP.factory.energy.EnergyManager;

import java.util.UUID;

public final class Factory extends JavaPlugin {

    public static UUID FACTORY_FAKE_PLAYER =
            UUID.fromString("00000000-0000-0000-0000-000000000001");

    public static Factory instance;
    public FactoryGUI factoryGUI;

    @Override
    public void onEnable() {
        instance = this;

        FactoryItems.init();

        startGeneratorScheduler();

        getLogger().info("Factory plugin enabled");

        this.getCommand("energygui").setExecutor(new EnergyGUICommand());
        this.getCommand("givefactory").setExecutor(new GiveFactoryCommand());
        this.getCommand("factoryconfig").setExecutor(new FactoryConfigCommand());

        getServer().getPluginManager().registerEvents(new FactoryListener(this), this);

        factoryGUI = new FactoryGUI(this);
    }

    /* =========================
       SCHEDULER GENERATORI
       ========================= */
    private void startGeneratorScheduler() {
        Bukkit.getScheduler().runTaskTimer(
                this,
                () -> {
                    Bukkit.getWorlds().forEach(world -> {
                        int minY = world.getMinHeight();
                        int maxY = world.getMaxHeight();
                        for (var chunk : world.getLoadedChunks()) {
                            int chunkX = chunk.getX() << 4;
                            int chunkZ = chunk.getZ() << 4;
                            for (int x = 0; x < 16; x++) {
                                for (int z = 0; z < 16; z++) {
                                    for (int y = minY; y < maxY; y++) {
                                        Block block = world.getBlockAt(chunkX + x, y, chunkZ + z);
                                        if (!(block.getState() instanceof TileState state)) continue;
                                        String type = state.getPersistentDataContainer()
                                                .get(FactoryItems.FACTORY_KEY, PersistentDataType.STRING);
                                        if (type == null || !type.startsWith("GENERATOR")) continue;
                                        GeneratorType genType = switch (type) {
                                            case "GENERATOR_OIL" -> GeneratorType.OIL;
                                            case "GENERATOR_COAL" -> GeneratorType.COAL;
                                            case "GENERATOR_SOLAR" -> GeneratorType.SOLAR;
                                            case "GENERATOR_WIND" -> GeneratorType.WIND;
                                            default -> null;
                                        };
                                        if (genType == null) continue;
                                        GeneratorMachine gen =
                                                new GeneratorMachine(block, genType);
                                        gen.tick();
                                    }
                                }
                            }
                        }
                    });
                },
                20L,     // delay iniziale (1 secondo)
                100L     // ogni 5 secondi
        );
    }

    @Override
    public void onDisable() {
        getLogger().info("Factory plugin spento.");
    }
}
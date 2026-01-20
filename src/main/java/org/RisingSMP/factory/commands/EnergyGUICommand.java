package org.RisingSMP.factory.commands;

import org.RisingSMP.factory.Factory;
import org.RisingSMP.factory.gui.EnergyGUI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class EnergyGUICommand implements CommandExecutor {

    // Mappa per tenere traccia dei GUI aperti dai player
    private final Map<Player, EnergyGUI> openGUIs = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return false;

        EnergyGUI gui = new EnergyGUI();
        player.openInventory(gui.getInventory());

        // Salva la GUI per aggiornamenti futuri
        openGUIs.put(player, gui);

        // Scheduler per aggiornamento automatico ogni secondo
        Bukkit.getScheduler().runTaskTimer(Factory.instance, () -> {
            if (!player.isOnline() || !player.getOpenInventory().getTopInventory().equals(gui.getInventory())) {
                openGUIs.remove(player);
                return;
            }
            gui.updateGUI();
        }, 0L, 20L);

        return true;
    }
}
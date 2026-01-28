package org.RisingSMP.factory.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.Material;

import org.RisingSMP.factory.gui.FactoryCentralGUI;
import org.RisingSMP.factory.gui.GeneratorGUI;
import org.RisingSMP.factory.gui.RefineryGUI;
import org.RisingSMP.factory.gui.InputGUI;

public class FactoryCentralGUIListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player player)) return;

        /* ========= FACTORY CENTRAL GUI ========= */
        if (event.getView().getTitle().equals("Factory Control Panel")) {

            event.setCancelled(true);

            if (event.getCurrentItem() == null) return;

            switch (event.getCurrentItem().getType()) {

                case DISPENSER -> {
                    player.sendMessage("§6[Input] Blocco di input selezionato!");
                    player.sendMessage("§7Estrae item dalle chest e li deposita sopra");
                    player.openInventory(new InputGUI().getInventory());
                }

                case HOPPER -> {
                    player.sendMessage("§6[Output] Blocco di output selezionato!");
                    player.sendMessage("§7Trasferisce item alle chest più vicine");
                    player.sendMessage("§7Massimo 1 blocco di distanza");
                }

                case REDSTONE_BLOCK -> {
                    player.openInventory(new GeneratorGUI().getInventory());
                }

                case IRON_BLOCK -> {
                    player.openInventory(new RefineryGUI().getInventory());
                }

                case CRAFTING_TABLE -> {
                    player.sendMessage("§a[Factory] Fabbriche Intermedie");
                }

                case ANVIL -> {
                    player.sendMessage("§e[Factory] Fabbriche Finali");
                }

                case BOW -> {
                    player.sendMessage("§6Musket selezionato (simulato)");
                }

                case ARROW -> {
                    player.sendMessage("§eBullet selezionato (simulato)");
                }
            }
            return;
        }

        /* ========= GENERATOR GUI ========= */
        if (event.getView().getTitle().equals("Generator Control")) {

            event.setCancelled(true);

            if (event.getCurrentItem() == null) return;

            switch (event.getCurrentItem().getType()) {

                case EMERALD_BLOCK -> {
                    player.sendMessage("§a[Generator] Upgrade simulato!");
                }

                case BARRIER -> {
                    player.openInventory(new FactoryCentralGUI().getInventory());
                }
            }
            return;
        }

        /* ========= REFINERY GUI ========= */
        if (event.getView().getTitle().equals("Refinery Control")) {

            event.setCancelled(true);

            if (event.getCurrentItem() == null) return;

            switch (event.getCurrentItem().getType()) {

                case EMERALD_BLOCK -> {
                    player.sendMessage("§a[Refinery] Upgrade simulato!");
                }

                case BARRIER -> {
                    player.openInventory(new FactoryCentralGUI().getInventory());
                }
            }
        }
    }


    // Metodo duplicato rimosso - ora tutto gestito in onInventoryClick
}

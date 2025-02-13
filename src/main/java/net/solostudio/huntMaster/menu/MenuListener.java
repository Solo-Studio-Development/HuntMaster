package net.solostudio.huntMaster.menu;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public class MenuListener implements Listener {
    @EventHandler
    public void onClick(final @NotNull InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof Menu menu) {
            event.setCancelled(true);
            menu.handleMenu(event);
        }
    }
}

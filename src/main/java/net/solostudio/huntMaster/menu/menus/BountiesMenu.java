package net.solostudio.huntMaster.menu.menus;

import net.solostudio.huntMaster.HuntMaster;
import net.solostudio.huntMaster.enums.keys.ConfigKeys;
import net.solostudio.huntMaster.enums.keys.MessageKeys;
import net.solostudio.huntMaster.item.ItemFactory;
import net.solostudio.huntMaster.managers.BountyData;
import net.solostudio.huntMaster.managers.MenuController;
import net.solostudio.huntMaster.menu.PaginatedMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@SuppressWarnings("all")
public class BountiesMenu extends PaginatedMenu {
    public BountiesMenu(MenuController menuController) {
        super(menuController);
    }

    @Override
    public String getMenuName() {
        return ConfigKeys.MENU_TITLE.getString();
    }

    @Override
    public int getSlots() {
        return ConfigKeys.MENU_SIZE.getInt();
    }

    @Override
    public int getMenuTick() {
        return ConfigKeys.MENU_TICK.getInt() * 20;
    }

    @Override
    public boolean enableFillerGlass() {
        return ConfigKeys.MENU_FILLER_GLASS.getBoolean();
    }

    @Override
    public void handleMenu(final InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!event.getInventory().equals(inventory)) return;

        event.setCancelled(true);

        if (event.getSlot() == ConfigKeys.FORWARD_SLOT.getInt()) {
            int nextPageIndex = page + 1;
            int totalPages = (int) Math.ceil((double) HuntMaster.getDatabase().getBounties().size() / getMaxItemsPerPage());

            if (nextPageIndex >= totalPages) {
                player.sendMessage(MessageKeys.LAST_PAGE.getMessage());
                return;
            } else {
                page++;
                super.open();
            }
        }

        if (event.getSlot() == ConfigKeys.BACK_SLOT.getInt()) {
            if (page == 0) {
                player.sendMessage(MessageKeys.FIRST_PAGE.getMessage());
            } else {
                page--;
                super.open();
            }
        }
    }

    @Override
    public void setMenuItems() {
        List<BountyData> bounties = HuntMaster.getDatabase().getBounties();

        inventory.clear();
        addMenuBorder();

        int startIndex = page * getMaxItemsPerPage();
        int endIndex = Math.min(startIndex + getMaxItemsPerPage(), bounties.size());

        IntStream
                .range(startIndex, endIndex)
                .forEach(index -> inventory.addItem(createBountyItem(bounties.get(index))));
    }

    @EventHandler
    public void onClose(final InventoryCloseEvent event) {
        if (event.getInventory().equals(inventory)) close();
    }

    private static ItemStack createBountyItem(@NotNull BountyData bounty) {
        ItemStack itemStack = ItemFactory.createItemFromString("bounty-item");
        ItemMeta meta = itemStack.getItemMeta();

        if (meta != null) {
            String displayName = meta
                    .getDisplayName()
                    .replace("{target}", bounty.target())
                    .replace("{id}", String.valueOf(bounty.id()));
            meta.setDisplayName(displayName);

            List<String> lore = meta.getLore();
            if (lore != null) {
                List<String> replacedLore = new ArrayList<>();

                lore.forEach(line -> {
                    replacedLore.add(line
                            .replace("{streak}", String.valueOf(HuntMaster.getDatabase().getStreak(Bukkit.getOfflinePlayer(bounty.target()))))
                            .replace("{reward_type}", bounty.rewardType().name())
                            .replace("{reward}", String.valueOf(bounty.reward()))
                            .replace("{player}", bounty.player()));
                });

                meta.setLore(replacedLore);
            }
            itemStack.setItemMeta(meta);
        }
        return itemStack;
    }
}

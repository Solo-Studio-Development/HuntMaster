package net.solostudio.huntMaster.enums.keys;

import net.solostudio.huntMaster.item.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public enum ItemKeys {
    BACK_ITEM("menu.back-item"),
    BOUNTYFINDER_ITEM("bountyfinder-item"),
    FILLER_GLASS_ITEM("menu.filler-glass-item"),
    FORWARD_ITEM("menu.forward-item");

    private final String path;

    ItemKeys(@NotNull final String path) {
        this.path = path;
    }

    public ItemStack getItem() {
        return ItemFactory.createItemFromString(path);
    };
}

package net.solostudio.huntMaster.menu;

import lombok.Getter;
import net.solostudio.huntMaster.enums.keys.ConfigKeys;
import net.solostudio.huntMaster.enums.keys.ItemKeys;
import net.solostudio.huntMaster.managers.MenuController;
import org.jetbrains.annotations.NotNull;

public abstract class PaginatedMenu extends Menu {
    @Getter protected int maxItemsPerPage = ConfigKeys.MENU_SIZE.getInt() - 2;
    protected int page = 0;

    public PaginatedMenu(@NotNull MenuController menuController) {
        super(menuController);
    }

    public void addMenuBorder() {
        inventory.setItem(ConfigKeys.BACK_SLOT.getInt(), ItemKeys.BACK_ITEM.getItem());
        inventory.setItem(ConfigKeys.FORWARD_SLOT.getInt(), ItemKeys.FORWARD_ITEM.getItem());
    }
}

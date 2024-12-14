package net.solostudio.huntMaster.events;

import lombok.Getter;
import net.solostudio.huntMaster.interfaces.PlaceholderProvider;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
public class BountyRemoveEvent extends Event implements PlaceholderProvider {
    private static final HandlerList handlers = new HandlerList();
    private final Player sender;
    private final Player target;

    public BountyRemoveEvent(@Nullable Player sender, @NotNull Player target) {
        this.sender = sender;
        this.target = target;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }


    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public Map<String, String> getPlaceholders() {
        Map<String, String> placeholders = new HashMap<>();

        placeholders.put("{target}", Objects.requireNonNull(target.getName()));

        return placeholders;
    }
}

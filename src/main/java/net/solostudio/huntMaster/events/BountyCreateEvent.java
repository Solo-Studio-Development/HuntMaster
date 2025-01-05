package net.solostudio.huntMaster.events;

import lombok.Getter;
import net.solostudio.huntMaster.HuntMaster;
import net.solostudio.huntMaster.enums.RewardTypes;
import net.solostudio.huntMaster.enums.keys.ConfigKeys;
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
public class BountyCreateEvent extends Event implements PlaceholderProvider {
    private static final HandlerList handlers = new HandlerList();
    private final Player sender;
    private final Player target;
    private final int reward;
    private final RewardTypes rewardType;

    public BountyCreateEvent(@Nullable Player sender, @NotNull Player target, int reward, @NotNull RewardTypes rewardType) {
        this.sender = sender;
        this.target = target;
        this.reward = reward;
        this.rewardType = rewardType;
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

        if (HuntMaster.getDatabase().isSenderIsRandom(target)) {
            placeholders.put("{sender}", Objects.requireNonNull(ConfigKeys.WEBHOOK_RANDOM_SENDER.getWebhookString()));
            placeholders.put("{target}", Objects.requireNonNull(target.getName()));
            placeholders.put("{reward}", Objects.requireNonNull(String.valueOf(reward)));
            placeholders.put("{rewardType}", Objects.requireNonNull(String.valueOf(rewardType)));
        }

        placeholders.put("{sender}", Objects.requireNonNull(Objects.requireNonNull(sender).getName()));
        placeholders.put("{target}", Objects.requireNonNull(target.getName()));
        placeholders.put("{reward}", Objects.requireNonNull(String.valueOf(reward)));
        placeholders.put("{rewardType}", Objects.requireNonNull(String.valueOf(rewardType)));

        return placeholders;
    }
}

package net.solostudio.huntMaster.enums.keys;

import net.solostudio.huntMaster.HuntMaster;
import net.solostudio.huntMaster.processor.MessageProcessor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public enum ConfigKeys {
    LANGUAGE("language"),
    DATABASE("database.type"),
    ALIASES("aliases"),

    MENU_SIZE("menu.size"),
    BACK_SLOT("menu.back-item.slot"),
    FORWARD_SLOT("menu.forward-item.slot"),
    MENU_TICK("menu.update-tick"),
    MENU_TITLE("menu.title"),
    MENU_FILLER_GLASS("menu.filler-glass"),

    YES("placeholder.yes"),
    NO("placeholder.no"),

    BASIC_FORMAT_M("formatting.basic.million"),
    BASIC_FORMAT_K("formatting.basic.thousand"),
    FORMATTING_TYPE("formatting.type"),
    FORMATTING_ENABLED("formatting.enabled"),

    MAXIMUM_TOP("max.top-to-get"),
    MAXIMUM_BOUNTY("max.bounty-per-player"),
    MAX_REWARD_LIMIT("max.rewardlimit"),
    RANDOM_BOUNTY_ENABLED("feature.random-bounty.enabled"),
    RANDOM_BOUNTY_REWARDTYPE("feature.random-bounty.rewardType"),
    RANDOM_BOUNTY_REWARD("feature.random-bounty.reward"),
    RANDOM_BOUNTY_PLAYER_VALUE("feature.random-bounty.player-value"),
    RANDOM_BOUNTY_PER_SECOND("feature.random-bounty.per-second"),
    NO_BOUNTY_NEARBY("feature.bountyfinder.no-bounty-nearby"),
    NEAREST_BOUNTY("feature.bountyfinder.nearest-bounty"),
    BOUNTYFINDER_ENABLED("bountyfinder-item.enabled"),
    SET_BOUNTY_ON_DEATH("set-bounty-on-death"),

    DEPENDENCY_LEVEL("dependency.level.enabled"),
    DEPENDENCY_LEVEL_MAX("dependency.level.max"),
    DEPENDENCY_LEVEL_MIN("dependency.level.min"),
    DEPENDENCY_MONEY_MIN("dependency.money.min"),
    DEPENDENCY_MONEY_MAX("dependency.money.max"),

    GLOWING_ENABLED("feature.glowing.enabled"),
    GLOWING_COLOR("feature.glowing.color"),

    WEBHOOK_RANDOM_SENDER("webhook.random-sender"),

    DISTANCE_ENABLED("feature.distance-tracker.enabled");

    private final String path;

    ConfigKeys(@NotNull String path) {
        this.path = path;
    }

    public String getString() {
        return MessageProcessor.process(HuntMaster.getInstance().getConfiguration().getString(path));
    }

    public String getWebhookString() {
        return HuntMaster.getInstance().getWebhookFile().getString(path);
    }

    public int getInt() {
        return HuntMaster.getInstance().getConfiguration().getInt(path);
    }

    public boolean getBoolean() {
        return HuntMaster.getInstance().getConfiguration().getBoolean(path);
    }


    public List<String> getList() {
        return HuntMaster.getInstance().getConfiguration().getList(path);
    }
}

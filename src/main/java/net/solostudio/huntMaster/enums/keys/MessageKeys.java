package net.solostudio.huntMaster.enums.keys;

import lombok.Getter;
import net.solostudio.huntMaster.HuntMaster;
import net.solostudio.huntMaster.processor.MessageProcessor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
public enum MessageKeys {
    NO_PERMISSION("messages.no-permission"),
    RELOAD("messages.reload"),
    HELP("messages.help"),
    PLAYER_REQUIRED("messages.player-required"),

    FIRST_PAGE("messages.first-page"),
    LAST_PAGE("messages.last-page"),

    INVALID_REWARDTYPE("messages.invalid-rewardtype"),
    INVALID_REWARDLIMIT("messages.invalid-rewardlimit"),
    INVALID_NUMBER("messages.invalid-number"),
    PLAYER_NOT_FOUND("messages.player-not-found"),
    CANT_BE_YOURSELF("messages.cant-be-yourself"),
    ALREADY_BOUNTY("messages.already-bounty"),
    NO_NEGATIVE("messages.no-negative"),
    INVALID_PLAYER("messages.invalid-player"),
    MISSING_ARGUMENT("messages.missing-argument"),

    SUCCESSFUL_SET("messages.successful-set"),
    BOUNTY_SET("messages.bounty-set-broadcast"),
    BOUNTY_RAISE("messages.bounty-raise-broadcast"),
    BOUNTY_DEAD_EVERYONE("messages.bounty-dead-everyone"),
    BOUNTY_DEAD_TARGET("messages.bounty-dead-target"),
    BOUNTY_DEAD_KILLER("messages.bounty-dead-killer"),

    NOT_ENOUGH_TOKEN("messages.not-enough-token"),
    NOT_ENOUGH_MONEY("messages.not-enough-money"),
    NOT_ENOUGH_PLAYERPOINTS("messages.not-enough-playerpoints"),
    NOT_ENOUGH_COINSENGINE("messages.not-enough-coinsengine"),
    NOT_ENOUGH_LEVEL("messages.not-enough-level"),

    MAX_TOP("messages.maximum-top"),
    TOP_HEADER("messages.top.header"),
    TOP_MESSAGE("messages.top.message"),

    MAX_BOUNTY("messages.max-bounty"),
    FEATURE_DISABLED("messages.feature-disabled"),
    NOT_BOUNTY("messages.not-a-bounty"),
    NOT_MATCHING_OWNERS("messages.not-matching-owners"),
    FEATURE_DISABLED_EVENT("messages.feature-disabled-event"),

    REMOVE_PLAYER("messages.successful-remove-player"),
    TARGET_RAISE("messages.target-raise"),
    PLAYER_RAISE("messages.player-raise"),
    FULL_INVENTORY("messages.full-inventory"),
    SUCCESSFUL_TAKEOFF_PLAYER("messages.successful-takeoff-player"),
    SUCCESSFUL_TAKEOFF_TARGET("messages.successful-takeoff-target"),
    REMOVE_TARGET("messages.successful-remove-target"),

    BOUNTY_FINDER_GIVEN("messages.bounty-finder-given"),
    ITEM_ALREADY_IN_INVENTORY("messages.item-already-in-inventory"),
    BOUNTY_FINDER_RECEIVED("messages.bounty-finder-received");

    private final String path;

    MessageKeys(@NotNull String path) {
        this.path = path;
    }

    public String getMessage() {
        return MessageProcessor.process(HuntMaster.getInstance().getLanguage().getString(path));
    }

    public List<String> getMessages() {
        return HuntMaster.getInstance().getLanguage().getList(path)
                .stream()
                .map(MessageProcessor::process)
                .toList();
    }
}

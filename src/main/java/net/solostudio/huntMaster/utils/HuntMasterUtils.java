package net.solostudio.huntMaster.utils;

import com.github.Anon8281.universalScheduler.utils.JavaUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import net.solostudio.huntMaster.HuntMaster;
import net.solostudio.huntMaster.enums.FormatTypes;
import net.solostudio.huntMaster.enums.RewardTypes;
import net.solostudio.huntMaster.enums.keys.ConfigKeys;
import net.solostudio.huntMaster.enums.keys.MessageKeys;
import net.solostudio.huntMaster.hooks.plugins.Vault;
import net.solostudio.huntMaster.processor.MessageProcessor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.*;

@SuppressWarnings("deprecation")
public class HuntMasterUtils {
    public static boolean isFolia = JavaUtil.classExists("io.papermc.paper.threadedregions.RegionizedServer");

    public static void sendActionBar(@NotNull Player player, @NotNull String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(MessageProcessor.process(message)));
    }

    public static boolean hasItem(@NotNull Inventory inventory, @NotNull ItemStack item) {
        return Arrays.stream(inventory.getContents())
                .filter(Objects::nonNull)
                .anyMatch(stack -> stack.isSimilar(item));
    }

    public static boolean handleMoneyReward(@NotNull Player player, int reward) {
        Economy economy = Vault.getEconomy();

        if (economy.has(player, reward)) {
            economy.withdrawPlayer(player, reward);
            return true;
        }

        player.sendMessage(MessageKeys.NOT_ENOUGH_MONEY.getMessage());
        return false;
    }

    public static boolean handleLevelReward(@NotNull Player player, int reward) {
        if (player.getLevel() >= reward) {
            player.setLevel(player.getLevel() - reward);
            return true;
        }

        player.sendMessage(MessageKeys.NOT_ENOUGH_LEVEL.getMessage());
        return false;
    }

    public static @Nullable NamedTextColor getNamedTextColor(@NotNull String colorName) {
        return switch (colorName) {
            case "BLACK", "black" -> NamedTextColor.namedColor(0);
            case "DARK_BLUE", "dark_blue" -> NamedTextColor.namedColor(170);
            case "DARK_GREEN", "dark_green" -> NamedTextColor.namedColor(43520);
            case "DARK_AQUA", "dark_aqua" -> NamedTextColor.namedColor(43690);
            case "DARK_GRAY", "dark_gray" -> NamedTextColor.namedColor(5592405);
            case "BLUE", "blue" -> NamedTextColor.namedColor(5592575);
            case "GREEN", "green" -> NamedTextColor.namedColor(5635925);
            case "AQUA", "aqua" -> NamedTextColor.namedColor(5636095);
            case "DARK_RED", "dark_red" -> NamedTextColor.namedColor(11141120);
            case "DARK_PURPLE", "dark_purple" -> NamedTextColor.namedColor(11141290);
            case "GRAY", "gray" -> NamedTextColor.namedColor(11184810);
            case "RED", "red" -> NamedTextColor.namedColor(16733525);
            case "LIGHT_PURPLE", "light_purple" -> NamedTextColor.namedColor(16733695);
            case "GOLD", "gold" -> NamedTextColor.namedColor(16755200);
            case "YELLOW", "yellow" -> NamedTextColor.namedColor(16777045);
            case "WHITE", "white" -> NamedTextColor.namedColor(16777215);
            default -> null;
        };
    }


    public static int getMinimumReward(@NotNull RewardTypes rewardType) {
        return rewardType == RewardTypes.MONEY
                ? ConfigKeys.DEPENDENCY_MONEY_MIN.getInt()
                : ConfigKeys.DEPENDENCY_LEVEL_MIN.getInt();
    }

    public static int getMaximumReward(@NotNull RewardTypes rewardType) {
        return rewardType == RewardTypes.MONEY
                ? ConfigKeys.DEPENDENCY_MONEY_MAX.getInt()
                : ConfigKeys.DEPENDENCY_LEVEL_MAX.getInt();
    }

    public static String formatPrice(int price) {
        if (!ConfigKeys.FORMATTING_ENABLED.getBoolean()) return String.valueOf(price);

        FormatTypes formatType = FormatTypes.valueOf(ConfigKeys.FORMATTING_TYPE.getString());

        return switch (formatType) {
            case DOT, dot -> String.format("%,d", price).replace(",", ".");
            case COMMAS, commas -> String.format("%,d", price);
            case BASIC, basic -> StartingUtils.getBasicFormatOverrides().entrySet().stream()
                    .sorted(Collections.reverseOrder(Map.Entry.comparingByKey()))
                    .filter(entry -> price >= entry.getKey())
                    .findFirst()
                    .map(entry -> {
                        double formattedPrice = (double) price / entry.getKey();
                        return new DecimalFormat("#.#").format(formattedPrice) + entry.getValue();
                    })
                    .orElse(String.valueOf(price));
        };
    }

    public static void setBountyOnDeath(@NotNull Player killer, @NotNull Player victim) {
        if (ConfigKeys.SET_BOUNTY_ON_DEATH.getBoolean()) HuntMaster.getDatabase().createBounty(victim, killer, HuntMaster.getDatabase().getRewardType(victim), HuntMaster.getDatabase().getReward(victim));
    }

    public static void tryToRemoveGlowing(@NotNull Player player) {
        String playerName = player.getName();
        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam("huntmaster" + playerName);

        player.setGlowing(false);

        if (team != null && !isFolia) {
            team.removePlayer(player);
            team.unregister();
            player.setGlowing(false);
        }
    }

    public static void tryToSetGlowing(@NotNull Player player) {
        String playerName = player.getName();
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        if (isGlowingEnabled() && HuntMaster.getDatabase().isBounty(player) && !isFolia) {
            Team team = scoreboard.getTeam("huntmaster" + playerName);

            if (team != null) team.unregister();
            else {
                player.setGlowing(true);

                team = scoreboard.registerNewTeam("huntmaster" + playerName);

                team.color(HuntMasterUtils.getNamedTextColor(ConfigKeys.GLOWING_COLOR.getString()));
                team.addPlayer(player);
            }
        }
    }

    private static boolean isGlowingEnabled() {
        return ConfigKeys.GLOWING_ENABLED.getBoolean();
    }
}

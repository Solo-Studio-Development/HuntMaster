package net.solostudio.huntMaster.hooks.plugins;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.solostudio.huntMaster.HuntMaster;
import net.solostudio.huntMaster.enums.keys.ConfigKeys;
import net.solostudio.huntMaster.utils.HuntMasterUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@SuppressWarnings("deprecation")
public class PlaceholderAPI {
    public static boolean isRegistered = false;

    public static void registerHook() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderIntegration().register();
            isRegistered = true;
        }
    }

    private static class PlaceholderIntegration extends PlaceholderExpansion {
        @Override
        public @NotNull String getIdentifier() {
            return "hm";
        }

        @Override
        public @NotNull String getAuthor() {
            return "User-19fff";
        }

        @Override
        public @NotNull String getVersion() {
            return HuntMaster.getInstance().getDescription().getVersion();
        }

        @Override
        public boolean canRegister() {
            return true;
        }

        @Override
        public boolean persist() {
            return true;
        }

        @Override
        public String onPlaceholderRequest(@NotNull Player player, @NotNull String params) {
            if (params.equalsIgnoreCase("isbounty")) return getBountyStatus(player);
            if (params.startsWith("top_")) return getTopPlayer(params);
            if (params.startsWith("topstreak_")) return getTopStreak(params);
            if (params.startsWith("price_")) return getPlayerPrice(params);

            return switch (params.toLowerCase()) {
                case "price" -> getBountyPrice(player);
                case "rewardtype" -> getRewardType(player);
                case "sender" -> getBountySender(player);
                default -> "";
            };
        }

        private String getBountyStatus(@NotNull Player player) {
            return HuntMaster.getDatabase().isBounty(player)
                    ? ConfigKeys.YES.getString()
                    : ConfigKeys.NO.getString();
        }

        private String getTopPlayer(@NotNull String params) {
            return parsePosition(params, "top_")
                    .map(position -> Optional.ofNullable(HuntMaster.getDatabase().getTopStreakPlayer(position))
                            .orElse("---"))
                    .orElse("");
        }

        private String getTopStreak(@NotNull String params) {
            return parsePosition(params, "topstreak_")
                    .map(position -> {
                        int streak = HuntMaster.getDatabase().getTopStreak(position);
                        return streak != 0 ? String.valueOf(streak) : "---";
                    })
                    .orElse("");
        }

        private String getPlayerPrice(@NotNull String params) {
            return Optional.of(params.split("_"))
                    .filter(parts -> parts.length > 1)
                    .map(parts -> Bukkit.getPlayerExact(parts[1]))
                    .filter(player -> HuntMaster.getDatabase().isBounty(player))
                    .map(player -> HuntMasterUtils.formatPrice(HuntMaster.getDatabase().getReward(player)))
                    .orElse("---");
        }

        private String getBountyPrice(@NotNull Player player) {
            if (!HuntMaster.getDatabase().isBounty(player)) return "";
            return HuntMasterUtils.formatPrice(HuntMaster.getDatabase().getReward(player));
        }

        private String getRewardType(@NotNull Player player) {
            if (!HuntMaster.getDatabase().isBounty(player)) return "";
            return String.valueOf(HuntMaster.getDatabase().getRewardType(player));
        }

        private String getBountySender(@NotNull Player player) {
            if (!HuntMaster.getDatabase().isBounty(player)) return "";
            return Optional.ofNullable(HuntMaster.getDatabase().getSender(player))
                    .map(Player::getName)
                    .orElse("");
        }

        private Optional<Integer> parsePosition(@NotNull String params, @NotNull String prefix) {
            try {
                return Optional.of(Integer.parseInt(params.substring(prefix.length())));
            } catch (NumberFormatException exception) {
                return Optional.empty();
            }
        }
    }
}

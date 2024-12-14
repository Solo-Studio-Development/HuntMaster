package net.solostudio.huntMaster.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.solostudio.huntMaster.HuntMaster;
import net.solostudio.huntMaster.enums.keys.ConfigKeys;
import net.solostudio.huntMaster.utils.HuntMasterUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

@SuppressWarnings("deprecation")
public class PlaceholderAPI extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "cb";
    }

    @Override
    public @NotNull String getAuthor() {
        return "";
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
        if (params.equals("isbounty")) return HuntMaster.getDatabase().isBounty(player) ? ConfigKeys.YES.getString() : ConfigKeys.NO.getString();

        if (params.startsWith("top_")) {
            try {
                int position = Integer.parseInt(params.split("_")[1]);

                if (HuntMaster.getDatabase().getTopStreakPlayer(position) != null) return HuntMaster.getDatabase().getTopStreakPlayer(position);
                return "---";
            } catch (Exception exception) {
                return "";
            }
        }

        if (params.startsWith("topstreak_")) {
            try {
                int position = Integer.parseInt(params.split("_")[1]);

                if (HuntMaster.getDatabase().getTopStreak(position) != 0) return String.valueOf(HuntMaster.getDatabase().getTopStreak(position));
                return "---";
            } catch (Exception exception) {
                return "";
            }
        }

        if (params.startsWith("price_")) {
            try {
                Player selectedPlayer = Bukkit.getPlayerExact(String.valueOf(params.split("_")[1]));

                if (selectedPlayer != null && HuntMaster.getDatabase().isBounty(selectedPlayer)) return HuntMasterUtils.formatPrice(HuntMaster.getDatabase().getReward(selectedPlayer));
                return "---";
            } catch (Exception exception) {
                return "";
            }
        }

        return switch (params) {
            case "price" -> {
                if (!HuntMaster.getDatabase().isBounty(player)) yield "";
                else yield HuntMasterUtils.formatPrice(HuntMaster.getDatabase().getReward(player));
            }

            case "rewardtype" -> {
                if (!HuntMaster.getDatabase().isBounty(player)) yield "";
                else yield String.valueOf(HuntMaster.getDatabase().getRewardType(player));
            }

            case "sender" -> {
                if (!HuntMaster.getDatabase().isBounty(player)) yield "";
                else yield HuntMaster.getDatabase().getSender(player).getName();
            }

            default -> "";
        };
    }

    public static void registerHook() {
        new PlaceholderAPI().register();
    }
}

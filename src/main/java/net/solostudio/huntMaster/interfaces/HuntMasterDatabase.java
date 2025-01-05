package net.solostudio.huntMaster.interfaces;

import net.solostudio.huntMaster.enums.RewardTypes;
import net.solostudio.huntMaster.managers.BountyData;
import net.solostudio.huntMaster.managers.TopData;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface HuntMasterDatabase {
    void createTable();

    // Bounty Operations
    void createBounty(@NotNull Player player, @NotNull Player target, @NotNull RewardTypes rewardType, int reward);
    void createRandomBounty(@NotNull Player target, @NotNull RewardTypes rewardType, int reward);
    void changeReward(@NotNull Player player, int newReward);

    List<BountyData> getBounties();
    Player getSender(@NotNull Player player);
    void removeBounty(@NotNull Player player);

    // Streak and Top Operations
    int getStreak(@NotNull OfflinePlayer player);
    List<TopData> getTop(int number);
    String getTopStreakPlayer(int top);
    int getTopStreak(int top);

    // Reward Getter
    int getReward(@NotNull Player player);
    RewardTypes getRewardType(@NotNull Player player);

    // Connection Management
    void reconnect();
    boolean isConnected();
    void disconnect();

    // Booleans
    boolean isBounty(@NotNull Player player);
    boolean reachedMaximumBounty(@NotNull Player player);

    List<BountyData> getOwnBounties(@NotNull Player player);

    boolean isSenderIsRandom(@NotNull Player player);
}

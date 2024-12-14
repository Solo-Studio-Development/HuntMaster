package net.solostudio.huntMaster.managers;

import net.solostudio.huntMaster.enums.RewardTypes;
import org.jetbrains.annotations.NotNull;

public record BountyData(int id, @NotNull String player, @NotNull String target, @NotNull RewardTypes rewardType, int reward) {
}

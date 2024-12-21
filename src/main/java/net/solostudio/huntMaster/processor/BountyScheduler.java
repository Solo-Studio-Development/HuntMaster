package net.solostudio.huntMaster.processor;

import net.solostudio.huntMaster.HuntMaster;
import net.solostudio.huntMaster.enums.RewardTypes;
import net.solostudio.huntMaster.enums.keys.ConfigKeys;
import net.solostudio.huntMaster.utils.LoggerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class BountyScheduler {
    private final Random random = new Random();

    public void startScheduling() {
        if (!ConfigKeys.RANDOM_BOUNTY_ENABLED.getBoolean()) return;

        HuntMaster.getScheduler().runTaskTimer(this::addRandomBounty, 0, ConfigKeys.RANDOM_BOUNTY_PER_SECOND.getInt() * 20L);
    }

    private void addRandomBounty() {
        List<Player> playersWithoutBounty = Bukkit.getOnlinePlayers()
                .stream()
                .filter(player -> !HuntMaster.getDatabase().isBounty(player))
                .collect(Collectors.toList());

        if (playersWithoutBounty.isEmpty()) {
            LoggerUtils.info("No eligible players for random bounty.");
            return;
        }

        Player selectedPlayer = playersWithoutBounty.get(random.nextInt(playersWithoutBounty.size()));
        RewardTypes rewardType = RewardTypes.valueOf(ConfigKeys.RANDOM_BOUNTY_REWARDTYPE.getString());
        int rewardAmount = ConfigKeys.RANDOM_BOUNTY_REWARD.getInt();

        HuntMaster.getDatabase().createRandomBounty(selectedPlayer, rewardType, rewardAmount);
    }
}


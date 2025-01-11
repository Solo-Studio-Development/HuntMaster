package net.solostudio.huntMaster.listeners;

import net.solostudio.huntMaster.HuntMaster;
import net.solostudio.huntMaster.enums.keys.ConfigKeys;
import net.solostudio.huntMaster.enums.keys.MessageKeys;
import net.solostudio.huntMaster.events.BountyDeathEvent;
import net.solostudio.huntMaster.hooks.Webhook;
import net.solostudio.huntMaster.hooks.plugins.Vault;
import net.solostudio.huntMaster.utils.BossBarUtils;
import net.solostudio.huntMaster.utils.HuntMasterUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.io.IOException;
import java.net.URISyntaxException;

import static net.solostudio.huntMaster.utils.HuntMasterUtils.tryToRemoveGlowing;

public class BountyDeathListener implements Listener {
    @EventHandler
    public void onDeath(final PlayerDeathEvent event) {
        Player target = event.getEntity();
        Player killer = target.getKiller();

        if (killer != null) {
            if (HuntMaster.getDatabase().isBounty(target)) {
                if (killer == HuntMaster.getDatabase().getSender(target)) return;

                switch (HuntMaster.getDatabase().getRewardType(target)) {
                    case LEVEL -> {
                        if (ConfigKeys.DEPENDENCY_LEVEL.getBoolean()) killer.setLevel(killer.getLevel() + HuntMaster.getDatabase().getReward(target));
                        else {
                            Vault.getEconomy().depositPlayer(killer, HuntMaster.getDatabase().getReward(target));
                            killer.sendMessage(MessageKeys.FEATURE_DISABLED_EVENT.getMessage());
                        }
                    }

                    case MONEY -> Vault.getEconomy().depositPlayer(killer, HuntMaster.getDatabase().getReward(target));
                }


                if (!HuntMaster.getDatabase().isSenderIsRandom(target)) killer.sendMessage(MessageKeys.BOUNTY_DEAD_KILLER.getMessage());

                target.sendMessage(MessageKeys.BOUNTY_DEAD_TARGET.getMessage());

                Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(MessageKeys.BOUNTY_DEAD_EVERYONE
                        .getMessage()
                        .replace("{name}", target.getName())));

                HuntMasterUtils.setBountyOnDeath(killer, target);
                HuntMaster.getDatabase().removeBounty(target);
                BossBarUtils.removeBossBar(target);

                HuntMaster.getInstance().getServer().getPluginManager().callEvent(new BountyDeathEvent(killer, target,
                        HuntMaster.getDatabase().getReward(target),
                        HuntMaster.getDatabase().getRewardType(target), killer));
            }
        }
    }

    @EventHandler
    public void onDeath(final BountyDeathEvent event) throws IOException, URISyntaxException {
        Webhook.sendWebhookFromString("webhook.bounty-death-embed", event);
        tryToRemoveGlowing(event.getTarget());
    }
}

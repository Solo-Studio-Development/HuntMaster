package net.solostudio.huntMaster.listeners;

import net.solostudio.huntMaster.HuntMaster;
import net.solostudio.huntMaster.events.BountyCreateEvent;
import net.solostudio.huntMaster.events.BountyRemoveEvent;
import net.solostudio.huntMaster.hooks.Webhook;
import net.solostudio.huntMaster.utils.BossBarUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URISyntaxException;

import static net.solostudio.huntMaster.utils.HuntMasterUtils.*;

public class GlowingListener implements Listener {
    @EventHandler
    public void onRemove(final @NotNull BountyRemoveEvent event) throws IOException, URISyntaxException {
        Player target = event.getTarget();

        tryToRemoveGlowing(target);

        Webhook.sendWebhookFromString("webhook.bounty-remove-embed", event);
        BossBarUtils.removeBossBar(target);
    }

    @EventHandler
    public void onCreate(final BountyCreateEvent event) throws IOException, URISyntaxException {
        if (!isFolia) tryToSetGlowing(event.getTarget());

        Webhook.sendWebhookFromString("webhook.bounty-create-embed", event);
        BossBarUtils.startDistanceTracking(event.getSender(), event.getTarget());
    }

    @EventHandler
    public void onJoin(final @NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!isFolia) tryToSetGlowing(player);
        if (HuntMaster.getDatabase().isBounty(player)) BossBarUtils.startDistanceTracking(HuntMaster.getDatabase().getSender(player), player);
    }

    @EventHandler
    public void onRespawn(final @NotNull PlayerRespawnEvent event) {
        tryToSetGlowing(event.getPlayer());
    }
}

package net.solostudio.huntMaster.listeners;

import net.solostudio.huntMaster.events.BountyCreateEvent;
import net.solostudio.huntMaster.events.BountyRemoveEvent;
import net.solostudio.huntMaster.hooks.Webhook;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import java.io.IOException;
import java.net.URISyntaxException;

import static net.solostudio.huntMaster.utils.HuntMasterUtils.*;

public class GlowingListener implements Listener {
    @EventHandler
    public void onRemove(final BountyRemoveEvent event) throws IOException, URISyntaxException {
        tryToRemoveGlowing(event.getTarget());

        Webhook.sendWebhookFromString("webhook.bounty-remove-embed", event);
    }

    @EventHandler
    public void onCreate(final BountyCreateEvent event) throws IOException, URISyntaxException {
        if (!isFolia) tryToSetGlowing(event.getTarget());

        Webhook.sendWebhookFromString("webhook.bounty-create-embed", event);
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        tryToSetGlowing(event.getPlayer());
    }

    @EventHandler
    public void onRespawn(final PlayerRespawnEvent event) {
        tryToSetGlowing(event.getPlayer());
    }
}

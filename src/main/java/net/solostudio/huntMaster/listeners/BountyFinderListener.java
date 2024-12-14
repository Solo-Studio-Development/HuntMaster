package net.solostudio.huntMaster.listeners;

import net.solostudio.huntMaster.HuntMaster;
import net.solostudio.huntMaster.enums.keys.ConfigKeys;
import net.solostudio.huntMaster.enums.keys.ItemKeys;
import net.solostudio.huntMaster.utils.HuntMasterUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Optional;

public class BountyFinderListener implements Listener {
    @EventHandler
    public void onPlayerItemHeld(final PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItem(event.getNewSlot());

        if (!ConfigKeys.BOUNTYFINDER_ENABLED.getBoolean()) return;
        if (item == null) return;
        if (!item.equals(ItemKeys.BOUNTYFINDER_ITEM.getItem())) return;

        HuntMaster.getScheduler().runTaskTimer(() -> {
            if (player.getInventory().getItemInMainHand().equals(item)) HuntMasterUtils.sendActionBar(player, getNearestBountyInfo(player));
        }, 0, 10);
    }


    private String getNearestBountyInfo(@NotNull Player player) {
        Optional<Player> nearestBounty = Bukkit.getOnlinePlayers()
                .stream()
                .filter(otherPlayer -> !otherPlayer.equals(player))
                .filter(HuntMaster.getDatabase()::isBounty)
                .min(Comparator.comparingDouble(otherPlayer -> player.getLocation().distance(otherPlayer.getLocation())))
                .map(Player.class::cast);

        if (nearestBounty.isPresent()) {
            Player bountyPlayer = nearestBounty.get();

            return ConfigKeys.NEAREST_BOUNTY
                    .getString()
                    .replace("{bounty}", bountyPlayer.getName())
                    .replace("{distance}", String.valueOf(player.getLocation().distance(bountyPlayer.getLocation())));

        } else return ConfigKeys.NO_BOUNTY_NEARBY.getString();
    }
}

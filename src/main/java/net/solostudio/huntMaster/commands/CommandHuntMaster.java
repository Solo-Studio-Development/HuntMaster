package net.solostudio.huntMaster.commands;

import net.solostudio.huntMaster.HuntMaster;
import net.solostudio.huntMaster.annotations.Bounties;
import net.solostudio.huntMaster.annotations.OwnBounties;
import net.solostudio.huntMaster.annotations.Players;
import net.solostudio.huntMaster.database.AbstractDatabase;
import net.solostudio.huntMaster.enums.RewardTypes;
import net.solostudio.huntMaster.enums.keys.ConfigKeys;
import net.solostudio.huntMaster.enums.keys.ItemKeys;
import net.solostudio.huntMaster.enums.keys.MessageKeys;
import net.solostudio.huntMaster.events.BountyRemoveEvent;
import net.solostudio.huntMaster.hooks.plugins.Vault;
import net.solostudio.huntMaster.managers.MenuController;
import net.solostudio.huntMaster.managers.TopData;
import net.solostudio.huntMaster.menu.menus.BountiesMenu;
import net.solostudio.huntMaster.utils.HuntMasterUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import static net.solostudio.huntMaster.utils.HuntMasterUtils.handleLevelReward;
import static net.solostudio.huntMaster.utils.HuntMasterUtils.handleMoneyReward;

@SuppressWarnings("deprecation")
@Command({"bounty", "huntmaster"})
public class CommandHuntMaster {
    @CommandPlaceholder
    public void defaultCommand(@NotNull CommandSender sender) {
        help(sender);
    }

    @Subcommand("help")
    public void help(@NotNull CommandSender sender) {
        MessageKeys.HELP
                .getMessages()
                .forEach(sender::sendMessage);
    }

    @Subcommand("reload")
    @CommandPermission("huntmaster.reload")
    @Description("Reloads the plugin.")
    public void reload(@NotNull CommandSender sender) {
        HuntMaster.getInstance().getLanguage().reload();
        HuntMaster.getInstance().getConfiguration().reload();
        sender.sendMessage(MessageKeys.RELOAD.getMessage());
    }

    @Subcommand("streaktop")
    @CommandPermission("huntmaster.streaktop")
    @Description("Retrieves the streak top list.")
    public void streaktop(@NotNull CommandSender sender, @Default("5") int value) {
        if (value <= 0) {
            sender.sendMessage(MessageKeys.NO_NEGATIVE.getMessage());
            return;
        }

        if (value > 15) {
            sender.sendMessage(MessageKeys.MAX_TOP
                    .getMessage()
                    .replace("{top}", String.valueOf(ConfigKeys.MAXIMUM_TOP.getInt())));
            return;
        }

        sender.sendMessage(TopData.getTopStreak(value).toPlainText());
    }

    @Subcommand("menu")
    @CommandPermission("huntmaster.menu")
    @Description("Opens the huntmaster menu.")
    public void menu(@NotNull Player player) {
        new BountiesMenu(MenuController.getMenuUtils(player)).open();
    }

    @Subcommand("set")
    @CommandPermission("huntmaster.set")
    @Description("Puts a bounty on the target.")
    public void set(@NotNull Player player, @NotNull @Players Player target, RewardTypes rewardType, int reward) {
        AbstractDatabase databaseManager = HuntMaster.getDatabase();

        if (!target.isOnline()) {
            player.sendMessage(MessageKeys.PLAYER_NOT_FOUND.getMessage());
            return;
        }

        if (target == player) {
            player.sendMessage(MessageKeys.CANT_BE_YOURSELF.getMessage());
            return;
        }

        if (databaseManager.isBounty(target)) {
            player.sendMessage(MessageKeys.ALREADY_BOUNTY.getMessage());
            return;
        }

        if (reward <= 0) {
            player.sendMessage(MessageKeys.NO_NEGATIVE.getMessage());
            return;
        }


        if (databaseManager.reachedMaximumBounty(player)) {
            player.sendMessage(MessageKeys.MAX_BOUNTY.getMessage());
            return;
        }

        int minReward = HuntMasterUtils.getMinimumReward(rewardType);
        int maxReward = HuntMasterUtils.getMaximumReward(rewardType);

        if (reward < minReward || (maxReward != 0 && reward > maxReward)) {
            player.sendMessage(MessageKeys.INVALID_REWARDLIMIT.getMessage()
                    .replace("{min}", minReward < 0 ? "0" : String.valueOf(minReward))
                    .replace("{max}", maxReward == 0 ? "\\u221E" : String.valueOf(maxReward)));
            return;
        }

        boolean success = false;
        switch (rewardType) {
            case MONEY -> success = handleMoneyReward(player, reward);
            case LEVEL -> success = handleLevelReward(player, reward);
        }

        if (success) {
            databaseManager.createBounty(player, target, rewardType, reward);
            player.sendMessage(MessageKeys.SUCCESSFUL_SET.getMessage());
            Bukkit.broadcastMessage(MessageKeys.BOUNTY_SET.getMessage()
                    .replace("{target}", target.getName())
                    .replace("{reward}", String.valueOf(reward))
                    .replace("{rewardType}", rewardType.toString()));
        }
    }

    @Subcommand("remove")
    @CommandPermission("huntmaster.remove")
    @Description("Removes the bounty from the target.")
    public void remove(@NotNull Player player, @NotNull @Bounties Player target) {
        if (!target.isOnline()) {
            player.sendMessage(MessageKeys.PLAYER_NOT_FOUND.getMessage());
            return;
        }

        if (!HuntMaster.getDatabase().isBounty(target)) {
            player.sendMessage(MessageKeys.NOT_BOUNTY.getMessage());
            return;
        }

        HuntMaster.getDatabase().removeBounty(target);
        player.sendMessage(MessageKeys.REMOVE_PLAYER.getMessage());
        target.sendMessage(MessageKeys.REMOVE_TARGET.getMessage());
        HuntMaster.getInstance().getServer().getPluginManager().callEvent(new BountyRemoveEvent(player, target));
    }

    @Subcommand("raise")
    @CommandPermission("huntmaster.raise")
    @Description("Increases the bounty.")
    public void raise(@NotNull Player player, @NotNull @OwnBounties Player target, int increaseAmount) {
        if (!target.isOnline()) {
            player.sendMessage(MessageKeys.PLAYER_NOT_FOUND.getMessage());
            return;
        }

        if (!HuntMaster.getDatabase().isBounty(target)) {
            player.sendMessage(MessageKeys.NOT_BOUNTY.getMessage());
            return;
        }

        if (increaseAmount <= 0) {
            player.sendMessage(MessageKeys.NO_NEGATIVE.getMessage());
            return;
        }

        if (HuntMaster.getDatabase().getSender(target) != player) {
            player.sendMessage(MessageKeys.NOT_MATCHING_OWNERS.getMessage());
            return;
        }

        RewardTypes rewardType = HuntMaster.getDatabase().getRewardType(target);

        int minReward = HuntMasterUtils.getMinimumReward(rewardType);
        int maxReward = HuntMasterUtils.getMaximumReward(rewardType);

        int oldReward = HuntMaster.getDatabase().getReward(target);
        int newReward = oldReward + increaseAmount;

        if (maxReward != 0 && newReward > maxReward) {
            player.sendMessage(MessageKeys.INVALID_REWARDLIMIT.getMessage()
                    .replace("{min}", minReward < 0 ? "0" : String.valueOf(minReward))
                    .replace("{max}", String.valueOf(maxReward)));
            return;
        }

        boolean success = false;

        switch (rewardType) {
            case MONEY -> success = handleMoneyReward(player, increaseAmount);
            case LEVEL -> success = handleLevelReward(player, increaseAmount);
        }

        if (success) {
            HuntMaster.getDatabase().changeReward(target, newReward);
            player.sendMessage(MessageKeys.PLAYER_RAISE.getMessage());
            target.sendMessage(MessageKeys.TARGET_RAISE
                    .getMessage()
                    .replace("{old}", String.valueOf(oldReward))
                    .replace("{new}", String.valueOf(newReward)));

            Bukkit.broadcastMessage(MessageKeys.BOUNTY_RAISE
                    .getMessage()
                    .replace("{target}", target.getName())
                    .replace("{oldReward}", String.valueOf(oldReward))
                    .replace("{newReward}", String.valueOf(newReward)));
        }
    }

    @Subcommand("takeoff")
    @CommandPermission("huntmaster.takeoff")
    @Description("Forcibly removes the bounty from the given player.")
    public void takeOff(@NotNull Player player, @NotNull @Bounties Player target) {
        if (!target.isOnline()) {
            player.sendMessage(MessageKeys.PLAYER_NOT_FOUND.getMessage());
            return;
        }

        if (!HuntMaster.getDatabase().isBounty(target)) {
            player.sendMessage(MessageKeys.NOT_BOUNTY.getMessage());
            return;
        }

        if (HuntMaster.getDatabase().getSender(target) != player) {
            player.sendMessage(MessageKeys.NOT_MATCHING_OWNERS.getMessage());
            return;
        }


        switch (HuntMaster.getDatabase().getRewardType(target)) {
            case MONEY -> Vault.getEconomy().depositPlayer(player, HuntMaster.getDatabase().getReward(target));
            case LEVEL -> player.setLevel(player.getLevel() + HuntMaster.getDatabase().getReward(target));
        }

        player.sendMessage(MessageKeys.SUCCESSFUL_TAKEOFF_PLAYER
                .getMessage()
                .replace("{target}", target.getName()));

        target.sendMessage(MessageKeys.SUCCESSFUL_TAKEOFF_TARGET
                .getMessage()
                .replace("{player}", player.getName()));
        HuntMaster.getDatabase().removeBounty(target);
        HuntMaster.getInstance().getServer().getPluginManager().callEvent(new BountyRemoveEvent(player, target));
    }

    @Subcommand("bountyfinder")
    @CommandPermission("huntmaster.bountyfinder")
    @Description("Gives the player a bountyfinder.")
    public void giveBountyFinder(@NotNull Player player, @NotNull @Players @Default("me") Player target) {
        if (!ConfigKeys.BOUNTYFINDER_ENABLED.getBoolean()) {
            player.sendMessage(MessageKeys.FEATURE_DISABLED.getMessage());
            return;
        }

        if (!target.isOnline()) {
            player.sendMessage(MessageKeys.PLAYER_NOT_FOUND.getMessage());
            return;
        }

        if (target.getInventory().firstEmpty() == -1) {
            player.sendMessage(MessageKeys.FULL_INVENTORY.getMessage());
            return;
        }

        if (HuntMasterUtils.hasItem(target.getInventory(), ItemKeys.BOUNTYFINDER_ITEM.getItem())) {
            player.sendMessage(MessageKeys.ITEM_ALREADY_IN_INVENTORY.getMessage());
            return;
        }

        target.getInventory().addItem(ItemKeys.BOUNTYFINDER_ITEM.getItem());
        player.sendMessage(MessageKeys.BOUNTY_FINDER_GIVEN.getMessage().replace("{target}", target.getName()));
        target.sendMessage(MessageKeys.BOUNTY_FINDER_RECEIVED.getMessage());
    }
}

package net.solostudio.huntMaster.exceptions;

import net.solostudio.huntMaster.enums.keys.MessageKeys;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.exception.BukkitExceptionHandler;
import revxrsal.commands.bukkit.exception.InvalidPlayerException;
import revxrsal.commands.bukkit.exception.NonPlayerEntitiesException;
import revxrsal.commands.bukkit.exception.SenderNotPlayerException;
import revxrsal.commands.exception.InvalidIntegerException;
import revxrsal.commands.exception.MissingArgumentException;
import revxrsal.commands.exception.NoPermissionException;
import revxrsal.commands.node.ParameterNode;

public final class CommandExceptionHandler extends BukkitExceptionHandler {
    @Override
    public void onInvalidPlayer(InvalidPlayerException exception, @NotNull BukkitCommandActor actor) {
        actor.error(MessageKeys.INVALID_PLAYER.getMessage());
    }

    @Override
    public void onSenderNotPlayer(SenderNotPlayerException exception, @NotNull BukkitCommandActor actor) {
        actor.error(MessageKeys.PLAYER_REQUIRED.getMessage());
    }

    @Override
    public void onInvalidInteger(@NotNull InvalidIntegerException exception, @NotNull BukkitCommandActor actor) {
        actor.error(MessageKeys.INVALID_NUMBER.getMessage());
    }

    @Override
    public void onMissingArgument(@NotNull MissingArgumentException exception, @NotNull BukkitCommandActor actor, @NotNull ParameterNode<BukkitCommandActor, ?> parameter) {
        actor.error(MessageKeys.MISSING_ARGUMENT.getMessage().replace("{usage}", parameter.command().usage()));
    }

    @Override
    public void onNoPermission(@NotNull NoPermissionException exception, @NotNull BukkitCommandActor actor) {
        actor.error(MessageKeys.NO_PERMISSION.getMessage());
    }

    @Override
    public void onNonPlayerEntities(@NotNull NonPlayerEntitiesException exception, @NotNull BukkitCommandActor actor) {
        actor.error(MessageKeys.OFFLINE_PLAYER.getMessage());
    }
}

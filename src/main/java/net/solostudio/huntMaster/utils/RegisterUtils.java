package net.solostudio.huntMaster.utils;

import net.solostudio.huntMaster.HuntMaster;
import net.solostudio.huntMaster.commands.CommandHuntMaster;
import net.solostudio.huntMaster.exceptions.CommandExceptionHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.reflections.Reflections;
import revxrsal.commands.bukkit.BukkitCommandHandler;
import revxrsal.commands.bukkit.exception.InvalidPlayerException;
import revxrsal.commands.bukkit.exception.SenderNotPlayerException;
import revxrsal.commands.exception.InvalidNumberException;
import revxrsal.commands.exception.MissingArgumentException;
import revxrsal.commands.exception.NoPermissionException;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicInteger;

public class RegisterUtils {
    public static void registerListeners() {
        LoggerUtils.info("### Registering listeners... ###");

        AtomicInteger count = new AtomicInteger();

        new Reflections("net.solostudio.huntMaster")
                .getSubTypesOf(Listener.class)
                .forEach(listenerClass -> {
                    try {
                        Bukkit.getServer().getPluginManager().registerEvents(listenerClass.getDeclaredConstructor().newInstance(), HuntMaster.getInstance());
                        count.getAndIncrement();
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
                        LoggerUtils.error(exception.getMessage());
                    }
                });

        LoggerUtils.info("### Successfully registered {} listener. ###", count.get());
    }

    public static void registerCommands() {
        LoggerUtils.info("### Registering commands... ###");

        BukkitCommandHandler handler = BukkitCommandHandler.create(HuntMaster.getInstance());

        handler.register(new CommandHuntMaster());
        LoggerUtils.info("### Successfully registered {} command(s). ###", handler.getCommands().size());
        LoggerUtils.info("### Registering exception handlers... ###");
        handler.registerExceptionHandler(SenderNotPlayerException.class, CommandExceptionHandler::handleException);
        handler.registerExceptionHandler(InvalidNumberException.class, CommandExceptionHandler::handleException);
        handler.registerExceptionHandler(NoPermissionException.class, CommandExceptionHandler::handleException);
        handler.registerExceptionHandler(MissingArgumentException.class, CommandExceptionHandler::handleException);
        handler.registerExceptionHandler(InvalidPlayerException.class, CommandExceptionHandler::handleException);

        handler.registerBrigadier();
        LoggerUtils.info("### Successfully registered exception handlers... ###");
    }
}

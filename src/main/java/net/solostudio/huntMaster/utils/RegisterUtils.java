package net.solostudio.huntMaster.utils;

import net.solostudio.huntMaster.HuntMaster;
import net.solostudio.huntMaster.annotations.Bounties;
import net.solostudio.huntMaster.annotations.OwnBounties;
import net.solostudio.huntMaster.annotations.Players;
import net.solostudio.huntMaster.commands.CommandHuntMaster;
import net.solostudio.huntMaster.exceptions.CommandExceptionHandler;
import net.solostudio.huntMaster.listeners.BountyDeathListener;
import net.solostudio.huntMaster.listeners.BountyFinderListener;
import net.solostudio.huntMaster.listeners.GlowingListener;
import net.solostudio.huntMaster.managers.BountyData;
import net.solostudio.huntMaster.menu.MenuListener;
import org.bukkit.entity.Player;
import revxrsal.commands.bukkit.BukkitLamp;

import java.util.Objects;

public class RegisterUtils {
    public static void registerListeners() {
        LoggerUtils.info("### Registering listeners... ###");

        HuntMaster.getInstance().getServer().getPluginManager().registerEvents(new BountyDeathListener(), HuntMaster.getInstance());
        HuntMaster.getInstance().getServer().getPluginManager().registerEvents(new BountyFinderListener(), HuntMaster.getInstance());
        HuntMaster.getInstance().getServer().getPluginManager().registerEvents(new GlowingListener(), HuntMaster.getInstance());
        HuntMaster.getInstance().getServer().getPluginManager().registerEvents(new MenuListener(), HuntMaster.getInstance());

        LoggerUtils.info("### Successfully registered 4 listener. ###");
    }

    public static void registerCommands() {
        LoggerUtils.info("### Registering commands... ###");

        var lamp = BukkitLamp.builder(HuntMaster.getInstance())
                        .exceptionHandler(new CommandExceptionHandler())

                        .suggestionProviders(providers -> {
                            providers.addProviderForAnnotation(Players.class, player -> context -> HuntMaster.getInstance().getServer().getOnlinePlayers()
                                    .stream()
                                    .map(Player::getName)
                                    .toList());
                        })

                        .suggestionProviders(providers -> {
                            providers.addProviderForAnnotation(Bounties.class, player -> context -> HuntMaster.getDatabase().getBounties()
                                    .stream()
                                    .map(BountyData::target)
                                    .toList());
                        })

                        .suggestionProviders(providers -> {
                            providers.addProviderForAnnotation(OwnBounties.class, player -> context -> HuntMaster.getDatabase().getOwnBounties(Objects.requireNonNull(context.actor().asPlayer()))
                                .stream()
                                .map(BountyData::target)
                                .toList());
                        })

                        .build();

        lamp.register(new CommandHuntMaster());
        LoggerUtils.info("### Successfully registered exception handlers... ###");
    }
}

package net.solostudio.huntMaster.managers;

import net.solostudio.huntMaster.HuntMaster;
import net.solostudio.huntMaster.enums.keys.MessageKeys;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

@SuppressWarnings("deprecation")
public record TopData(@NotNull OfflinePlayer player, int streak) {
    public static TextComponent getTopStreak(int value) {
        List<TopData> topStreaks = HuntMaster.getDatabase().getTop(value);

        TextComponent message = new TextComponent(
                MessageKeys.TOP_HEADER
                        .getMessage()
                        .replace("{value}", String.valueOf(value))
        );
        message.addExtra("\n\n");

        IntStream.range(0, topStreaks.size()).forEach(index -> {
            TopData top = topStreaks.get(index);

            TextComponent playerMessage = new TextComponent(
                    MessageKeys.TOP_MESSAGE
                            .getMessage()
                            .replace("{streak}", String.valueOf(top.streak))
                            .replace("{name}", Objects.requireNonNull(top.player().getName()))
                            .replace("{place}", String.valueOf(index + 1))
            );

            message.addExtra(playerMessage);

            if (index < topStreaks.size() - 1) message.addExtra("\n");
        });

        return message;
    }

}

package net.solostudio.huntMaster.utils;

import com.github.Anon8281.universalScheduler.scheduling.tasks.MyScheduledTask;
import lombok.experimental.UtilityClass;
import net.solostudio.huntMaster.HuntMaster;
import net.solostudio.huntMaster.managers.DistanceColor;
import net.solostudio.huntMaster.processor.MessageProcessor;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@UtilityClass
public class BossBarUtils {
    public static Map<Player, BossBar> activeBossBars = new ConcurrentHashMap<>();
    private MyScheduledTask bossBarTask;
    private MyScheduledTask trackingTask;

    public static void createBossBar(@NotNull Player player, @NotNull String path) {
        var config = HuntMaster.getInstance().getConfiguration();

        if (!config.getBoolean(path + ".enabled")) return;

        var rawTitle = MessageProcessor.process(config.getString(path + ".title"));
        var style = BarStyle.valueOf(config.getString(path + ".style").toUpperCase());
        var flags = config.getList(path + ".flags");
        var bossBar = Bukkit.createBossBar(rawTitle, BarColor.PINK, style);

        activeBossBars.put(player, bossBar);
        flags.forEach(flag -> bossBar.addFlag(BarFlag.valueOf(flag.toUpperCase())));
        bossBar.setProgress(1.0);
        bossBar.addPlayer(player);
        bossBar.setVisible(true);

        bossBarTask = HuntMaster.getInstance().getScheduler().runTaskTimer(() -> {
            if (!activeBossBars.containsKey(player)) {
                bossBar.setVisible(false);
                bossBar.removeAll();
                bossBarTask.cancel();
            }
        }, 0, 20);
    }

    public static void removeBossBar(@NotNull Player player) {
        BossBar bossBar = activeBossBars.remove(player);

        if (bossBar == null) return;

        bossBar.setVisible(false);
        bossBar.removeAll();
        bossBarTask.cancel();
        trackingTask.cancel();
    }

    public static List<DistanceColor> loadDistanceColors() {
        var config = HuntMaster.getInstance().getConfiguration();

        return Objects.requireNonNull(config.getSection("feature.distance-tracker.colors"))
                .getValues(false)
                .entrySet()
                .stream()
                .map(entry -> new DistanceColor(Double.parseDouble(entry.getKey()), (String) entry.getValue()))
                .collect(Collectors.toList());
    }

    public static void startDistanceTracking(@NotNull Player hunter, @NotNull Player target) {
        List<DistanceColor> distanceColors = loadDistanceColors();

        trackingTask = HuntMaster.getInstance().getScheduler().runTaskTimer(() -> {
            if (!hunter.isOnline() || !target.isOnline()) {
                removeBossBar(target);
                return;
            }

            double distance = hunter.getLocation().distance(target.getLocation());
            var colorKey = getColorKey(distanceColors, distance);

            if (!activeBossBars.containsKey(target)) {
                createBossBar(target, "feature.distance-tracker");
            }

            activeBossBars.get(target).setColor(BarColor.valueOf(colorKey.toUpperCase()));
        }, 0, 20);
    }

    private static String getColorKey(List<DistanceColor> distanceColors, double distance) {
        return distanceColors.stream()
                .filter(dc -> distance < dc.distance())
                .map(DistanceColor::color)
                .findFirst()
                .orElse("GREEN");
    }
}

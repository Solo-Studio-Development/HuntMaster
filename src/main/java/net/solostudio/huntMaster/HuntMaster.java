package net.solostudio.huntMaster;

import com.github.Anon8281.universalScheduler.UniversalScheduler;
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import lombok.Getter;
import net.solostudio.huntMaster.config.Config;
import net.solostudio.huntMaster.language.Language;
import net.solostudio.huntMaster.utils.LoggerUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;

import static net.solostudio.huntMaster.utils.StartingUtils.initialize;
import static net.solostudio.huntMaster.utils.StartingUtils.saveResourceIfNotExists;

public final class HuntMaster extends JavaPlugin {
    @Getter private static HuntMaster instance;
    @Getter private static TaskScheduler scheduler;
    @Getter private Language language;
    private Config config;

    @Override
    public void onLoad() {
        instance = this;
        scheduler = UniversalScheduler.getScheduler(this);

        try {
            initialize();
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException exception) {
            LoggerUtils.error(exception.getMessage());
        }
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();


    }

    private void initializeComponents() {
        config = new Config();

        saveResourceIfNotExists("locales/messages_en.yml");
        saveResourceIfNotExists("locales/messages_de.yml");

        language = new Language("messages_" + LanguageTypes.valueOf(ConfigKeys.LANGUAGE.getString()));
    }

    @Override
    public void onDisable() {
    }
}

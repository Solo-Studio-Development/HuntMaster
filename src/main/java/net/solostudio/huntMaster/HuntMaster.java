package net.solostudio.huntMaster;

import com.github.Anon8281.universalScheduler.UniversalScheduler;
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import lombok.Getter;
import net.solostudio.huntMaster.config.Config;
import net.solostudio.huntMaster.hooks.WebhookFile;
import net.solostudio.huntMaster.interfaces.HuntMasterDatabase;
import net.solostudio.huntMaster.database.H2;
import net.solostudio.huntMaster.database.MySQL;
import net.solostudio.huntMaster.enums.DatabaseTypes;
import net.solostudio.huntMaster.enums.LanguageTypes;
import net.solostudio.huntMaster.enums.keys.ConfigKeys;
import net.solostudio.huntMaster.language.Language;
import net.solostudio.huntMaster.processor.BountyScheduler;
import net.solostudio.huntMaster.update.SpigotUpdateFetcher;
import net.solostudio.huntMaster.utils.LoggerUtils;
import org.bstats.bukkit.Metrics;
import revxrsal.zapper.ZapperJavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;

import static net.solostudio.huntMaster.hooks.plugins.PlaceholderAPI.registerHook;
import static net.solostudio.huntMaster.update.SpigotUpdateFetcher.checkUpdates;
import static net.solostudio.huntMaster.utils.StartingUtils.*;

public final class HuntMaster extends ZapperJavaPlugin {
    private final int BSTATS_ID = 24140;

    @Getter private static HuntMaster instance;
    @Getter private static HuntMasterDatabase database;
    @Getter private TaskScheduler scheduler;
    @Getter private Language language;
    @Getter private WebhookFile webhookFile;
    private Config config;

    @Override
    public void onLoad() {
        instance = this;
        scheduler = UniversalScheduler.getScheduler(this);
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        initializeComponents();
        initializeDatabaseManager();
        loadBasicFormatOverrides();
        checkUpdates();

        try {
            initialize();
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException exception) {
            LoggerUtils.error(exception.getMessage());
        }

        new Metrics(this, BSTATS_ID);
        registerHook();
        new BountyScheduler().startScheduling();
    }

    @Override
    public void onDisable() {
        if (database != null) database.disconnect();
    }

    public Config getConfiguration() {
        return config;
    }

    private void initializeComponents() {
        config = new Config();

        saveResourceIfNotExists("locales/messages_en.yml");
        saveResourceIfNotExists("locales/messages_de.yml");
        saveResourceIfNotExists("config.yml");
        saveResourceIfNotExists("settings/webhook.yml");

        language = new Language("messages_" + LanguageTypes.valueOf(ConfigKeys.LANGUAGE.getString()));
        webhookFile = new WebhookFile();

        getConfiguration().updateConfigWithDefaults();
        getLanguage().updateConfigWithDefaults();
        getWebhookFile().updateConfigWithDefaults();
    }

    private void initializeDatabaseManager() {
        try {
            switch (DatabaseTypes.valueOf(ConfigKeys.DATABASE.getString().toUpperCase())) {
                case MYSQL -> {
                    LoggerUtils.info("### MySQL support found! Starting to initialize it... ###");
                    database = new MySQL(Objects.requireNonNull(getConfiguration().getSection("database.mysql")));
                    database.createTable();
                    LoggerUtils.info("### MySQL database has been successfully initialized! ###");
                }
                case H2 -> {
                    LoggerUtils.info("### H2 support found! Starting to initialize it... ###");
                    database = new H2();
                    database.createTable();
                    LoggerUtils.info("### H2 database has been successfully initialized! ###");
                }
                default -> throw new SQLException("Unsupported database type!");
            }
        } catch (SQLException | ClassNotFoundException exception) {
            LoggerUtils.error("Database initialization failed: {}", exception.getMessage());
        }
    }
}

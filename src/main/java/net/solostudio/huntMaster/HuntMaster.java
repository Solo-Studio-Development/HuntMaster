package net.solostudio.huntMaster;

import com.github.Anon8281.universalScheduler.UniversalScheduler;
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import lombok.Getter;
import net.solostudio.huntMaster.config.Config;
import net.solostudio.huntMaster.database.AbstractDatabase;
import net.solostudio.huntMaster.database.MySQL;
import net.solostudio.huntMaster.database.SQLite;
import net.solostudio.huntMaster.enums.DatabaseTypes;
import net.solostudio.huntMaster.enums.LanguageTypes;
import net.solostudio.huntMaster.enums.keys.ConfigKeys;
import net.solostudio.huntMaster.hooks.PlaceholderAPI;
import net.solostudio.huntMaster.language.Language;
import net.solostudio.huntMaster.processor.BountyScheduler;
import net.solostudio.huntMaster.utils.LoggerUtils;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Objects;

import static net.solostudio.huntMaster.hooks.PlaceholderAPI.registerHook;
import static net.solostudio.huntMaster.utils.StartingUtils.*;

public final class HuntMaster extends JavaPlugin {
    @Getter private static HuntMaster instance;
    @Getter private static TaskScheduler scheduler;
    @Getter private Language language;
    @Getter private static AbstractDatabase database;
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
        registerHook();
        loadBasicFormatOverrides();

        new BountyScheduler().startScheduling();

        //  updates

        try {
            initialize();
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException exception) {
            LoggerUtils.error(exception.getMessage());
        }

        new Metrics(this, 24140);
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

        language = new Language("messages_" + LanguageTypes.valueOf(ConfigKeys.LANGUAGE.getString()));
    }

    private void initializeDatabaseManager() {
        try {
            switch (DatabaseTypes.valueOf(ConfigKeys.DATABASE.getString().toUpperCase())) {
                case MYSQL -> {
                    LoggerUtils.info("### MySQL support found! Starting to initializing it... ###");
                    database = new MySQL(Objects.requireNonNull(getConfiguration().getSection("database.mysql")));
                    MySQL mySQL = (MySQL) database;

                    mySQL.createTable();
                    LoggerUtils.info("### MySQL database has been successfully initialized! ###");
                }
                case SQLITE -> {
                    LoggerUtils.info("### SQLite support found! Starting to initializing it... ###");
                    database = new SQLite();
                    SQLite sqlite = (SQLite) database;

                    sqlite.createTable();
                    LoggerUtils.info("### SQLite database has been successfully initialized! ###");
                }
                default -> throw new SQLException("Unsupported database type!");
            }
        } catch (SQLException | ClassNotFoundException exception) {
            LoggerUtils.error("Database initialization failed: {}", exception.getMessage());
        }
    }
}

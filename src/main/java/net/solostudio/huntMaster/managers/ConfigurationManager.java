package net.solostudio.huntMaster.managers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.solostudio.huntMaster.HuntMaster;
import net.solostudio.huntMaster.processor.MessageProcessor;
import net.solostudio.huntMaster.utils.LoggerUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class ConfigurationManager {
    @Getter private YamlConfiguration yml;
    @Getter private String name;
    private File config;

    public ConfigurationManager(@NotNull String dir, @NotNull String name) {
        File file = new File(dir);

        if (!file.exists()) {
            if (!file.mkdirs()) return;
        }

        config = new File(dir, name + ".yml");

        if (!config.exists()) {
            try {
                if (!config.createNewFile()) return;
            } catch (IOException exception) {
                LoggerUtils.error(exception.getMessage());
            }
        }

        yml = YamlConfiguration.loadConfiguration(config);

        yml.options().copyDefaults(true);

        this.name = name;
    }

    public void reload() {
        yml = YamlConfiguration.loadConfiguration(config);
        updateConfigWithDefaults();

        save();
    }

    public void set(@NotNull String path, Object value) {
        yml.set(path, value);
        save();
    }

    public void save() {
        try {
            yml.save(config);
        } catch (IOException exception) {
            LoggerUtils.error(exception.getMessage());
        }
    }

    public List<String> getList(@NotNull String path) {
        return yml.getStringList(path)
                .stream()
                .map(MessageProcessor::process)
                .toList();

    }

    public boolean getBoolean(@NotNull String path) {
        return yml.getBoolean(path);
    }

    public int getInt(@NotNull String path) {
        return yml.getInt(path);
    }

    public String getString(@NotNull String path) {
        return yml.getString(path);
    }

    public @Nullable ConfigurationSection getSection(@NotNull String path) {
        return yml.getConfigurationSection(path);
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public void updateConfigWithDefaults() {
        InputStream defaultConfigStream = HuntMaster.getInstance().getResource(name + ".yml");

        if (defaultConfigStream == null) return;

        YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultConfigStream));

        defaultConfig.getKeys(true).forEach(key -> {
            if (yml.contains(key)) return;

            yml.set(key, defaultConfig.get(key));
        });

        save();
    }
}

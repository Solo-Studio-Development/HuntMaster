package net.solostudio.huntMaster.language;

import net.solostudio.huntMaster.HuntMaster;
import net.solostudio.huntMaster.managers.ConfigurationManager;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class Language extends ConfigurationManager {
    public Language(@NotNull String name) {
        super(HuntMaster.getInstance().getDataFolder().getPath() + File.separator + "locales", name);
        save();
    }
}

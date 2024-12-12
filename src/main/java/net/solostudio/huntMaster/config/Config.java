package net.solostudio.huntMaster.config;

import net.solostudio.huntMaster.HuntMaster;
import net.solostudio.huntMaster.managers.ConfigurationManager;

public class Config extends ConfigurationManager {
    public Config() {
        super(HuntMaster.getInstance().getDataFolder().getPath(), "config");
        save();
    }
}

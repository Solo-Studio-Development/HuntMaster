package net.solostudio.huntMaster.hooks;

import net.solostudio.huntMaster.HuntMaster;
import net.solostudio.huntMaster.managers.ConfigurationManager;

import java.io.File;

public class WebhookFile extends ConfigurationManager {
    public WebhookFile() {
        super(HuntMaster.getInstance().getDataFolder().getPath() + File.separator + "settings", "webhook");
        save();
    }
}

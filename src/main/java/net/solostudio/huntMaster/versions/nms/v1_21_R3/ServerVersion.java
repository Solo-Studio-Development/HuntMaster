package net.solostudio.huntMaster.versions.nms.v1_21_R3;

import net.solostudio.huntMaster.interfaces.ServerVersionSupport;
import net.solostudio.huntMaster.utils.LoggerUtils;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ServerVersion implements ServerVersionSupport {
    @Contract(pure = true)
    public ServerVersion(@NotNull Plugin plugin) {
        LoggerUtils.info("### Loading support for version 1.21.3 and 1.21.4... ###");
        LoggerUtils.info("### Support for 1.21.3 and 1.21.4 is loaded! ###");
    }

    @Override
    public String getName() {
        return "1.21_R3";
    }

    @Override
    public boolean isSupported() {
        return true;
    }
}

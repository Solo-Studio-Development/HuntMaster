package net.solostudio.huntMaster.hooks.plugins;

import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import net.solostudio.huntMaster.HuntMaster;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Vault {
    @Getter
    private static Economy economy = null;

    private Vault() {}

    private static void setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = HuntMaster.getInstance().getServer().getServicesManager().getRegistration(Economy.class);

        if (rsp != null) economy = rsp.getProvider();
    }

    static {
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) setupEconomy();
    }
}

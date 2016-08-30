package net.hcfactions.duels;

import org.bukkit.plugin.java.JavaPlugin;

public class DuelsPlugin extends JavaPlugin {

    // Properties

    public static int duelInviteTimeout;

    private DuelManager duelManager;

    public void onEnable() {
        saveDefaultConfig();
        duelManager = new DuelManager(this);

        duelInviteTimeout = getConfig().getInt("General.DuelInviteTimeout", 10);
    }

    public DuelManager getDuelManager() {
        return duelManager;
    }

}
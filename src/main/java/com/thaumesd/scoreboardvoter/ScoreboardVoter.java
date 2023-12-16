package com.thaumesd.scoreboardvoter;
import com.thaumesd.scoreboardvoter.manager.ScoreboardVoterManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ScoreboardVoter extends JavaPlugin {

    private I18n i18nModule;
    private final ScoreboardVoterManager scoreboardVoterManager = new ScoreboardVoterManager(this);

    public ScoreboardVoterManager getScoreboardVoterManager() {
        return scoreboardVoterManager;
    }

    @Override
    public void onEnable(){
        saveDefaultConfig();

        i18nModule = new I18n(this);
        i18nModule.onEnable();

        new Commands(this).registerCommands();

        getLogger().info("Plugin is enabled");
    }

    @Override
    public void onDisable() {
        i18nModule.onDisable();
        getLogger().info("Plugin is disabled");
    }

}

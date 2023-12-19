package com.thaumesd.scoreboardvoter.runnable;

import com.thaumesd.scoreboardvoter.ScoreboardVoter;
import com.thaumesd.scoreboardvoter.manager.ScoreboardVoterManager;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Consumer;

import static com.thaumesd.scoreboardvoter.I18n.translate;

public class VoterRunnable implements Consumer<BukkitTask> {

    private final ScoreboardVoter plugin;
    private final ScoreboardVoterManager scoreboardVoterManager;

    public VoterRunnable(ScoreboardVoter plugin, ScoreboardVoterManager scoreboardVoterManager) {
        this.plugin = plugin;
        this.scoreboardVoterManager = scoreboardVoterManager;
    }

    @Override
    public void accept(BukkitTask task) {
        scoreboardVoterManager.decrementCounter();
        if (scoreboardVoterManager.getCounter() == 0) {
            plugin.getServer().broadcast(translate("voteEnded", true, scoreboardVoterManager.getVotedYes(), scoreboardVoterManager.getVotedNo()));

            if (scoreboardVoterManager.getVotedYes() > scoreboardVoterManager.getVotedNo()) {
                scoreboardVoterManager.setSidebar(scoreboardVoterManager.getCurrentObjective());
                plugin.getServer().broadcast(translate("setScoreboard", true));
            } else {
                plugin.getServer().broadcast(translate("notSetScoreboard", true));
            }

            scoreboardVoterManager.resetVote();
            task.cancel();
        }
    }
}

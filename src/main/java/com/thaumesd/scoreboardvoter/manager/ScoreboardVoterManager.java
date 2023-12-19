package com.thaumesd.scoreboardvoter.manager;

import com.thaumesd.scoreboardvoter.ScoreboardVoter;
import com.thaumesd.scoreboardvoter.runnable.VoterRunnable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.UUID;

import static com.thaumesd.scoreboardvoter.I18n.translate;

public class ScoreboardVoterManager {
    private final ScoreboardVoter plugin;
    private boolean isEnabled;
    private String currentObjective;
    private int counter;
    private int votedYes;
    private int votedNo;
    private final ArrayList<UUID> players;

    public ScoreboardVoterManager(ScoreboardVoter plugin) {
        this.plugin = plugin;
        this.isEnabled = true;
        this.votedYes = 0;
        this.votedNo = 0;
        this.players = new ArrayList<>();
        this.currentObjective = null;
        this.counter = plugin.getConfig().getInt("vote-session-time", 30);
    }

    public boolean getIsEnabled() {
        return isEnabled;
    }

    public ArrayList<UUID> getPlayers() {
        return players;
    }

    public int getVotedYes(){
        return votedYes;
    }

    public int getVotedNo() {
        return votedNo;
    }

    public ArrayList<Objective> getObjectives() {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        return new ArrayList<>(scoreboard.getObjectives());
    }

    public void resetCurrentScoreboard() {
        Bukkit.getScoreboardManager().getMainScoreboard().clearSlot(DisplaySlot.SIDEBAR);
    }

    public String getCurrentObjective() {
        return currentObjective;
    }

    public void resetVote(){
        isEnabled = true;
        counter = plugin.getConfig().getInt("vote-session-time", 30);
        votedYes = 0;
        votedNo = 0;
        players.clear();
    }

    public int getCounter(){
        return counter;
    }

    public void decrementCounter() {
        counter--;
    }

    public void startVote(String currentObjective) {
        isEnabled = false;
        this.currentObjective = currentObjective;
        plugin.getServer().broadcast(translate("voteStarted", currentObjective, counter));
        BukkitScheduler scheduler = plugin.getServer().getScheduler();
        scheduler.runTaskTimer(plugin, new VoterRunnable(plugin, this), 0, 20);
    }


    public void setSidebar(String currentObjective) {
        Objective objective = getObjective(currentObjective);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public Objective getObjective(String name) {
        for (Objective objective : getObjectives()) {
            if (objective.getName().equalsIgnoreCase(name)) {
                return objective;
            }
        }
        return null;
    }

    public void vote(Player player, boolean agree) {
        players.add(player.getUniqueId());
        if (agree) {
            plugin.getServer().broadcast(translate("playerHasVotedYes", player.getName()));
            votedYes++;
        } else {
            plugin.getServer().broadcast(translate("playerHasVotedNo", player.getName()));
            votedNo++;
        }
    }
}

package com.thaumesd.scoreboardvoter;

import cloud.commandframework.Command;
import cloud.commandframework.CommandHelpHandler;
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.InvalidSyntaxException;
import cloud.commandframework.exceptions.NoPermissionException;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.paper.PaperCommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;

import java.util.ArrayList;
import java.util.List;

import static com.thaumesd.scoreboardvoter.I18n.translate;

public class Commands {

    private final ScoreboardVoter plugin;
    private final PaperCommandManager<CommandSender> commandManager;

    private final CommandHelpHandler<CommandSender> minecraftHelpHandler;

    public Commands(ScoreboardVoter plugin) {
        this.plugin = plugin;
        this.commandManager = createCommandManager(plugin);
        this.minecraftHelpHandler = this.commandManager.createCommandHelpHandler();
    }

    private static PaperCommandManager<CommandSender> createCommandManager(ScoreboardVoter plugin) {
        PaperCommandManager<CommandSender> commandManager;
        try {
            commandManager = PaperCommandManager.createNative(plugin, CommandExecutionCoordinator.simpleCoordinator());
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }

        commandManager.registerExceptionHandler(InvalidSyntaxException.class, (commandSender, e) -> commandSender.sendMessage(translate("invalidSyntax", true)));
        commandManager.registerExceptionHandler(NoPermissionException.class, (commandSender, e) -> commandSender.sendMessage(translate("invalidPermission", true)));

        commandManager.registerBrigadier();

        return commandManager;
    }

    public void registerCommands() {
        final Command.Builder<CommandSender> commandBuilder = this.commandManager.commandBuilder("sc", "scoreboardvoter");
        this.commandManager.command(commandBuilder.literal("help").meta(CommandMeta.DESCRIPTION, "View all scoreboard voter commands.").permission("scoreboardvoter.command.help").handler(this::help));
        this.commandManager.command(commandBuilder.literal("version").meta(CommandMeta.DESCRIPTION, "View the plugin version.").permission("scoreboardvoter.command.version").handler(this::version));
        this.commandManager.command(commandBuilder.literal("reset").meta(CommandMeta.DESCRIPTION, "Reset the current active sidebar scoreboard.").permission("scoreboardvoter.command.reset").handler(this::resetScoreboard));
        this.commandManager.command(commandBuilder.literal("start").meta(CommandMeta.DESCRIPTION, "Start a vote for an objective.").permission("scoreboardvoter.command.start").argument(StringArgument.<CommandSender>builder("scoreboard").single().withSuggestionsProvider(plugin.getScoreboardVoterManager()::getObjectiveNames)).handler(this::startVote));
        this.commandManager.command(commandBuilder.literal("list").meta(CommandMeta.DESCRIPTION, "List all objectives on the server.").permission("scoreboardvoter.command.list").argument(IntegerArgument.optional("page", 1)).handler(this::listScoreboards));
        this.commandManager.command(commandBuilder.literal("vote").meta(CommandMeta.DESCRIPTION, "Vote on an objective.").senderType(Player.class).permission("scoreboardvoter.command.vote").argument(StringArgument.<CommandSender>builder("decision").single().withSuggestionsProvider(plugin.getScoreboardVoterManager()::getDecisions)).handler(this::vote));
    }

    private void version(final CommandContext<CommandSender> context) {
        List<String> authors = plugin.getPluginMeta().getAuthors();
        String version = plugin.getPluginMeta().getVersion();
        context.getSender().sendMessage(translate("pluginInfo", false, String.join(", ", authors), version));
    }

    private void resetScoreboard(final CommandContext<CommandSender> context) {
        plugin.getScoreboardVoterManager().resetCurrentScoreboard();
        context.getSender().sendMessage(translate("resetScoreboard", true));
    }

    private void startVote(final CommandContext<CommandSender> context) {
        String scoreboardName = context.get("scoreboard");
        CommandSender sender = context.getSender();

        if (plugin.getScoreboardVoterManager().getObjective(scoreboardName) == null) {
            sender.sendMessage(translate("unknownScoreboardObjectives", true));
            return;
        }

        if (!plugin.getScoreboardVoterManager().getIsEnabled()) {
            sender.sendMessage(translate("voteInProgress", true));
            return;
        }

        plugin.getScoreboardVoterManager().startVote(scoreboardName);
    }

    private void listScoreboards(final CommandContext<CommandSender> context) {
        ArrayList<Objective> objectives = plugin.getScoreboardVoterManager().getObjectives();
        CommandSender sender = context.getSender();

        if (objectives.isEmpty()) {
            sender.sendMessage(translate("noObjectives", true));
            return;
        }

        int page = context.get("page");
        int listSize = plugin.getConfig().getInt("list-size", 10);
        int finalPage = (int) Math.ceil(objectives.size() / (double) listSize);
        if (page > finalPage || page <= 0) {
            sender.sendMessage(translate("pageNotFound", true));
            return;
        }
        int index = (page - 1) * listSize;
        int endIndex = index + listSize;
        if (endIndex > objectives.size()) {
            endIndex = objectives.size();
        }
        sender.sendMessage(translate("listHeader", false));
        sender.sendMessage(translate("listTopContent", false));
        sender.sendMessage(translate("listSeparator", false));
        for (int i = index; i < endIndex; i++) {
            sender.sendMessage(translate("listItem", false, objectives.get(i).getName(), objectives.get(i).getName()));
        }
        sender.sendMessage(translate("listPageCounter",false, page, finalPage));
        sender.sendMessage(translate("listFooter", false));
    }

    private void vote(CommandContext<CommandSender> context) {
        String decision = context.get("decision");
        final Player player = (Player) context.getSender();
        if (plugin.getScoreboardVoterManager().getIsEnabled()) {
            player.sendMessage(translate("noVoteInProgress", true));
            return;
        }
        if (plugin.getScoreboardVoterManager().getPlayers().contains(player.getUniqueId())) {
            player.sendMessage(translate("hasAlreadyVoted", true));
            return;
        }

        if (decision.equalsIgnoreCase("yes") || decision.equalsIgnoreCase("y")) {
            plugin.getScoreboardVoterManager().vote(player, true);
            return;
        }

        if (decision.equalsIgnoreCase("no") || decision.equalsIgnoreCase("n")) {
            plugin.getScoreboardVoterManager().vote(player, false);
            return;
        }

        player.sendMessage(translate("voteUsage", true));
    }

    private void help(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();
        sender.sendMessage(translate("listHeader", false));
        minecraftHelpHandler.getAllCommands().forEach(helpEntry -> sender.sendMessage(translate("listCommandItem", false, helpEntry.getSyntaxString(), helpEntry.getDescription())));
        sender.sendMessage(translate("listFooter", false));
    }

}

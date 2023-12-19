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
import java.util.stream.Collectors;

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

        commandManager.registerExceptionHandler(InvalidSyntaxException.class, (commandSender, e) -> commandSender.sendMessage(translate("invalidSyntax")));
        commandManager.registerExceptionHandler(NoPermissionException.class, (commandSender, e) -> commandSender.sendMessage(translate("invalidPermission")));

        commandManager.registerBrigadier();

        return commandManager;
    }

    public List<String> getObjectiveNames(CommandContext<CommandSender> commandSenderCommandContext, String s) {
        return plugin.getScoreboardVoterManager().getObjectives().stream().map(Objective::getName).collect(Collectors.toList());
    }

    public void registerCommands() {
        final Command.Builder<CommandSender> commandBuilder = this.commandManager.commandBuilder("sc", "scoreboardvoter");
        this.commandManager.command(commandBuilder.literal("version").meta(CommandMeta.DESCRIPTION, "View the plugin version.").permission("scoreboardvoter.command.version").handler(this::version));
        this.commandManager.command(commandBuilder.literal("reset").meta(CommandMeta.DESCRIPTION, "Reset the current active scoreboard.").permission("scoreboardvoter.command.reset").handler(this::resetScoreboard));
        this.commandManager.command(commandBuilder.literal("start").meta(CommandMeta.DESCRIPTION, "Start a vote for a scoreboard.").permission("scoreboardvoter.command.start").argument(StringArgument.<CommandSender>builder("scoreboard").single().withSuggestionsProvider(this::getObjectiveNames)).handler(this::startVote));
        this.commandManager.command(commandBuilder.literal("list").meta(CommandMeta.DESCRIPTION, "List all objectives on the server.").permission("scoreboardvoter.command.list").argument(IntegerArgument.optional("page", 1)).handler(this::listScoreboards));
        this.commandManager.command(commandBuilder.literal("vote").meta(CommandMeta.DESCRIPTION, "Vote on an objective.").senderType(Player.class).permission("scoreboardvoter.command.list").argument(StringArgument.single("decision")).handler(this::vote));
        this.commandManager.command(commandBuilder.literal("help").meta(CommandMeta.DESCRIPTION, "View all commands.").handler(context -> {
            CommandSender sender = context.getSender();
            sender.sendMessage(translate("listHeader"));
            minecraftHelpHandler.getAllCommands().forEach(helpEntry -> sender.sendMessage(translate("listCommandItem", helpEntry.getSyntaxString(), helpEntry.getDescription())));
            sender.sendMessage(translate("listFooter"));
        }));
    }

    private void version(final CommandContext<CommandSender> context) {
        List<String> authors = plugin.getPluginMeta().getAuthors();
        String version = plugin.getPluginMeta().getVersion();
        context.getSender().sendMessage(translate("pluginInfo", String.join(", ", authors), version));
    }

    private void resetScoreboard(final CommandContext<CommandSender> context) {
        plugin.getScoreboardVoterManager().resetCurrentScoreboard();
        context.getSender().sendMessage(translate("resetScoreboard"));
    }

    private void startVote(final CommandContext<CommandSender> context) {
        String scoreboardName = context.get("scoreboard");
        CommandSender sender = context.getSender();

        if (plugin.getScoreboardVoterManager().getObjective(scoreboardName) == null) {
            sender.sendMessage(translate("unknownScoreboardObjectives"));
            return;
        }

        if (!plugin.getScoreboardVoterManager().getIsEnabled()) {
            sender.sendMessage(translate("voteInProgress"));
            return;
        }

        plugin.getScoreboardVoterManager().startVote(scoreboardName);
    }

    private void listScoreboards(final CommandContext<CommandSender> context) {
        ArrayList<Objective> objectives = plugin.getScoreboardVoterManager().getObjectives();
        CommandSender sender = context.getSender();

        if (objectives.isEmpty()) {
            sender.sendMessage(translate("noObjectives"));
            return;
        }

        int page = context.get("page");
        int listSize = plugin.getConfig().getInt("list-size", 10);
        int finalPage = (int) Math.ceil(objectives.size() / (double) listSize);
        if (page > finalPage || page <= 0) {
            sender.sendMessage(translate("pageNotFound"));
            return;
        }
        int index = (page - 1) * listSize;
        int endIndex = index + listSize;
        if (endIndex > objectives.size()) {
            endIndex = objectives.size();
        }
        sender.sendMessage(translate("listHeader"));
        sender.sendMessage(translate("listTopContent"));
        sender.sendMessage(translate("listSeparator"));
        for (int i = index; i < endIndex; i++) {
            sender.sendMessage(translate("listItem", objectives.get(i).getName(), objectives.get(i).getName()));
        }
        sender.sendMessage(translate("listPageCounter", page, finalPage));
        sender.sendMessage(translate("listFooter"));
    }

    private void vote(CommandContext<CommandSender> ctx) {
        String decision = ctx.get("decision");
        final Player player = (Player) ctx.getSender();
        if (plugin.getScoreboardVoterManager().getIsEnabled()) {
            player.sendMessage(translate("noVoteInProgress"));
            return;
        }
        if (plugin.getScoreboardVoterManager().getPlayers().contains(player.getUniqueId())) {
            player.sendMessage(translate("hasAlreadyVoted"));
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

        player.sendMessage(translate("voteUsage"));
    }


}

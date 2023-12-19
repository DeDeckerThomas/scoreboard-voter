# Scoreboard-Voter
Scoreboard-Voter is a lightweight Minecraft Paper plugin that enhances gameplay by allowing players to vote on existing objectives within the sidebar scoreboard. Whether you want to gauge opinions on specific in-game achievements, challenges, or any other objectives, Scoreboard-Voter makes the process easy and efficient.

## Features
* Objective Voting: Enable players to cast their votes on existing objectives displayed in the sidebar scoreboard.
* Customizable Settings: Configure the plugin to suit your server's needs. Adjust voting durations and other settings through a straightforward configuration file. More features coming soon!

## Installation
1) Download the latest release of Scoreboard-Voter from the releases page. 
2) Place the JAR file into your server's plugins folder. 
3) Restart your server to enable the plugin.

## Usage
* Create Objectives: Set up objectives in your scoreboard that you want players to vote on. More information [here](https://minecraft.fandom.com/wiki/Scoreboard#Command_reference).
* Start a Vote: Use the `/sc start <objective>` command to initiate a vote for a specific objective. Players can view all existing scoreboards by using the `/sc list` command.
* Cast Votes: Players can cast their votes by using the `/sc vote <decision>` command during the voting period. When the voting period is over, then the sidebar scoreboard will be updated accordingly.
* Reset sidebar scoreboard by using the `/sc reset` command.

## Permissions
* `scoreboardvoter.command.help`: View all scoreboard voter commands.
* `scoreboardvoter.command.version`: View the plugin version.
* `scoreboardvoter.command.reset`: Reset the current active sidebar scoreboard.
* `scoreboardvoter.command.start`: Start a vote for an objective.
* `scoreboardvoter.command.list`: List all objectives on the server.
* `scoreboardvoter.command.vote`: Vote on an objective.

## Contributing
Contributions are welcome! If you have suggestions, bug reports, or want to contribute to the development of Scoreboard-Voter, please fork the repository and submit a pull request.

## License
This plugin is licensed under the GPL-3.0 License - see the [LICENSE](./LICENSE) file for details.
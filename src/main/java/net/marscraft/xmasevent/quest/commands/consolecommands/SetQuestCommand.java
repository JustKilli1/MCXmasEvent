package net.marscraft.xmasevent.quest.commands.consolecommands;

import net.marscraft.xmasevent.Main;
import net.marscraft.xmasevent.quest.commands.CommandState;
import net.marscraft.xmasevent.quest.commands.Commandmanager;
import net.marscraft.xmasevent.quest.commands.ICommandType;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import net.marscraft.xmasevent.shared.messagemanager.IMessagemanager;
import net.marscraft.xmasevent.shared.messagemanager.Messagemanager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetQuestCommand extends Commandmanager implements CommandExecutor {

    private ILogmanager _logger;
    private DatabaseAccessLayer _sql;
    private Main _plugin;
    private IMessagemanager _messages;

    public SetQuestCommand(ILogmanager logger, DatabaseAccessLayer sql, Main plugin) {
        super(logger);
        _logger = logger;
        _sql = sql;
        _plugin = plugin;
    }

    /*
    * Command: /setQuest [PlayerName] [args]
    * Handles /setQuest Command
    */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length < 2) return false;
        if(!(sender instanceof Player) && !(args[1].equalsIgnoreCase("next"))) return false;
        Player player = Bukkit.getPlayer(args[0]);
        if(player == null) return false;
        _messages = new Messagemanager(_logger, player);
        String commandOption = args[1].toLowerCase();
        switch (commandOption) {
            case "next":
                ICommandType commandType = new CommandTypeNext(_logger, _sql, player, _plugin);
                commandStateActions(commandType.ExecuteCommand(args), args);
                break;
        }

        return false;
    }

    /*
    * Sends Player Message based on commandState
    */
    private void commandStateActions(CommandState commandState, String[] args) {
        switch (commandState) {
            case InvalidPlayerName:
                _messages.SendPlayerMessage("Spieler §c" + args[0] + " §awurde nicht gefunden");
                break;
            case QuestFinished:
                _messages.SendPlayerMessage("Du hast die §cQuest §aerfolgreich abgeschlossen");
                break;
            case CouldNotFinishQuest:
                _logger.Error("Could not finish quest from " + args[0]);
                break;
            case NoActiveQuestFound:
                _logger.Info("No Active Quest for player " + args[0] + " found.");
                break;
            case TaskNotFinished:
                _messages.SendPlayerMessage("Du musst zuerst deine §cAufgabe §aerfüllen");
                break;
        }
    }
}

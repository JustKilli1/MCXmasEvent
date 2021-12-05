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
        ICommandType commandType;
        switch (commandOption) {
            case "next":
                commandType = new CommandTypeNext(_logger, _sql, player, _plugin);
                break;
            case "progress":
                commandType = new CommandTypeProgress(_logger, _sql, player, _plugin);
                break;
            default:
                return false;
        }
        commandStateActions(commandType.ExecuteCommand(args), args);
        return false;
    }

    /*
    * Sends Player Message based on commandState
    */
    private void commandStateActions(CommandState commandState, String[] args) {
        Player player = Bukkit.getPlayer(args[0]);
        int questId = _sql.GetActivePlayerQuestId(player);
        String npcName = _sql.GetQuestNpcName(questId);
        String questName = _sql.GetQuestName(questId);
        switch (commandState) {
            case InvalidPlayerName:
                _logger.Error("Player " + args[0] + " could not be found");
                break;
            case QuestFinished:
                _logger.Info("Player " + args[0] + " finished a quest");
                break;
            case CouldNotFinishQuest:
                _logger.Error("Could not finish quest from " + args[0]);
                break;
            case NoActiveQuestFound:
                _logger.Info("No Active Quest for player " + args[0] + " found.");
                break;
            case TaskNotFinished:
                _messages.SendNpcMessage(npcName, "Du musst erst den Quest §c" + questName + " §abeenden.");
                break;
            case TaskExecuted:
                _messages.SendNpcMessage(npcName, "Vielen dank für die Items");
        }
    }
}
